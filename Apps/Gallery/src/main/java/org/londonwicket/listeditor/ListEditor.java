package org.londonwicket.listeditor;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.londonwicket.js.JavaScriptBase;

/**
 * List Editor for generic objects.
 * <p>
 * You're expected to provide hooks for creating a new item in the list, and
 * creating the components to display each item in the list.
 * 
 * @param <T>
 *            Type of item to edit (String, File, etc.)
 */
public abstract class ListEditor<T> extends Panel {

  private static ResourceReference DELETE_ICON = new ResourceReference(
      ListEditor.class, "delete.png");

  private WebMarkupContainer listEditorContainer;
  private AbstractAjaxBehavior behaviour;

  public ListEditor(String id) {
    super(id);
    commonInit();
  }
  
  public ListEditor(String id, IModel model) {
    super(id, model);
    commonInit();
  }

  private void commonInit() {
    // Add the JavaScript we need.
    add(HeaderContributor.forJavaScript(JavaScriptBase.class, "prototype.js"));
    add(HeaderContributor.forJavaScript(JavaScriptBase.class, "scriptaculous.js"));
    add(HeaderContributor.forJavaScript(ListEditor.class, "ListEditor.js"));

    // Add default or overridden CSS.
    HeaderContributor css = getCss();
    if (css != null) {
      add(css);
    }

    listEditorContainer = new WebMarkupContainer("listEditorContainer");
    listEditorContainer.setOutputMarkupId(true);
    add(listEditorContainer);

    final ListView items = new ListView("item", getModel()) {
      @Override
      protected void populateItem(ListItem item) {
        int index = item.getIndex();
        item.setMarkupId(listEditorContainer.getMarkupId() + "_" + index);
        item.setOutputMarkupId(true);
        item.add(getItemComponent("component", item.getModel(), index));
        item.add(getDeleteLink("deleteLink", index));
      }
    };
    listEditorContainer.add(items);

    behaviour = new AbstractDefaultAjaxBehavior() {
      @Override
      protected void respond(AjaxRequestTarget target) {
        // Get the list of parameters, which will tell us the new order of the
        // items.
        String[] params = getRequest().getParameters(
            listEditorContainer.getMarkupId() + "[]");

        if (params != null) {
          List<T> items = (List<T>) getModelObject();
          List<T> originals = new ArrayList<T>(items);

          // For each of the provided drag and drop ordered items, grab the
          // corresponding index from the list copy, and insert it into
          // the original model list in the incrementally indexed position.
          for (int index = 0; index < items.size(); index++) {
            int newIndex = Integer.parseInt(params[index]);
            items.set(index, originals.get(newIndex));
          }
        }

        onSort(target);
        
        // Repaint the list and recreate the sortable so we have new indices.
        target.addComponent(listEditorContainer);
        target.appendJavascript(getCreateSortableJS());
      }

      @Override
      public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderOnDomReadyJavascript(getCreateSortableJS());
      }
    };
    listEditorContainer.add(behaviour);

    add(new AjaxFallbackLink("newLink") {
      private static final long serialVersionUID = 1L;

      @Override
      public void onClick(AjaxRequestTarget target) {
        T item = createItem(target);
        List<T> list = (List<T>) ListEditor.this.getModelObject();
        if (list == null) {
          list = new ArrayList<T>();
        }
        list.add(item);
        if (target != null) {
          // Redraw the list.
          target.addComponent(listEditorContainer);
          // Make it sortable.
          target.appendJavascript(getCreateSortableJS());
        }
      }
    });
  }

  /**
   * @return JavaScript to execute to create the Scriptaculous Sortable.
   */
  private String getCreateSortableJS() {
    return "createSortable('" + listEditorContainer.getMarkupId() + "', '"
        + behaviour.getCallbackUrl() + "');";
  }

  private Component getDeleteLink(final String id, final int itemIndex) {
    // Degrade as nicely as we can for users with no JavaScript.
    Link link = new AjaxFallbackLink(id) {
      @Override
      public void onClick(AjaxRequestTarget target) {
        List<T> list = (List<T>) ListEditor.this.getModelObject();
        T removedItem = list.remove(itemIndex);
        // Check for non-javascript capable browsers.
        if (target != null) {
          onDelete(target, removedItem);
          target.addComponent(listEditorContainer);
          target.appendJavascript(getCreateSortableJS());
        }
      }

    };
    link.add(new Image("deleteImage", DELETE_ICON));
    return link;
  }
  
  protected void onSort(AjaxRequestTarget target) {
    
  }
  
  protected void onDelete(AjaxRequestTarget target, T item) {
    
  }

  /**
   * Override this to change the default CSS.
   */
  protected HeaderContributor getCss() {
    return HeaderContributor.forCss(ListEditor.class, "style.css");
  }

  /**
   * Creates a Component for an item in the backing List.
   * 
   * @param id
   *            Component ID.
   * @param model
   *            Model for the item. getObject() will retrieve the appropriate
   *            item from the backing List.
   * @param index
   *            Index of the item in the list.
   * @return
   */
  protected abstract Component getItemComponent(String id, IModel model,
      int index);

  /**
   * Creates a new item for the List (called when "Add new..." link is clicked).
   * 
   * @param target
   * @return
   */
  protected abstract T createItem(AjaxRequestTarget target);
}
