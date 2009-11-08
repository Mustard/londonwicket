/*
 *    Copyright 2009 Richard Wilkinson

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.googlecode.londonwicket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;

public class ComponentExpression {

	private static final String ANY_COMPONENT_MATCHER = "*";
	private static final String ANY_COMPONENT_RECURSIVE_MATCHER = "**";

	public static Component findComponent(Component parent, String expression) {
		List<Component> results = findAllComponents(parent, expression);
		if (results.isEmpty()) {
			return null;
		} else {
			return results.get(0);
		}
	}

	public static Component findComponent(Component parent, String expression,
			Class<? extends Component> typeRestriction) {
		List<Component> results = findAllComponents(parent, expression,
				typeRestriction);
		if (results.isEmpty()) {
			return null;
		} else {
			return results.get(0);
		}
	}

	public static List<Component> findAllComponents(Component parent,
			String expression) {
		return findAllComponents(parent, expression, Component.class);

	}

	public static List<Component> findAllComponents(Component parent,
			String expression, Class<? extends Component> typeRestriction) {
		if (expression == null || expression.equals("")) {
			return Collections.emptyList();
		}
		return findComponent(parent, new LinkedList<String>(Arrays
				.asList(expression.split(Character
						.toString(Component.PATH_SEPARATOR)))), typeRestriction);
	}

	private static List<Component> findComponent(Component parent,
			LinkedList<String> expressionListIn,
			Class<? extends Component> typeRestriction) {

		LinkedList<String> expressionList = new LinkedList<String>(
				expressionListIn);

		if (expressionList.isEmpty()) {
			if (typeRestriction.isAssignableFrom(parent.getClass())) {
				return Arrays.asList(parent);
			} else {
				return Collections.emptyList();
			}
		} else {
			String first = expressionList.getFirst();
			if (!first.equals(ANY_COMPONENT_RECURSIVE_MATCHER)) {
				expressionList.removeFirst();
				List<Component> allMatches = getChild(parent, first);
				if (allMatches.isEmpty()) {
					return Collections.emptyList();
				} else {
					List<Component> finallyMatchedComponents = new ArrayList<Component>();
					for (Component aMatch : allMatches) {
						finallyMatchedComponents.addAll(findComponent(aMatch,
								expressionList, typeRestriction));
					}
					return finallyMatchedComponents;
				}
			} else if (expressionList.size() == 1) {
				List<Component> allMatches = new ArrayList<Component>();
				if (parent instanceof MarkupContainer) {
					for (Component aMatch : getAllChildren((MarkupContainer) parent)) {
						if (typeRestriction.isAssignableFrom(aMatch.getClass())) {
							allMatches.add(aMatch);
						}
						allMatches.addAll(findComponent(aMatch, expressionList,
								typeRestriction));
					}
					return allMatches;
				} else {
					return Collections.emptyList();
				}
			} else {
				List<Component> allMatches = new ArrayList<Component>();
				LinkedList<String> fake = new LinkedList<String>();
				fake.add(ANY_COMPONENT_RECURSIVE_MATCHER);
				List<Component> allPotentialParents = findComponent(parent,
						fake, Component.class);
				expressionList.removeFirst();
				for (Component aParent : allPotentialParents) {
					allMatches.addAll(findComponent(aParent, expressionList,
							typeRestriction));
				}
				return allMatches;
			}
		}
	}

	private static List<Component> getChild(Component parent, String expression) {

		if (parent instanceof MarkupContainer) {

			MarkupContainer parentContainer = (MarkupContainer) parent;
			if (expression.equals(ANY_COMPONENT_MATCHER)) {
				return getAllChildren(parentContainer);
			} else {

				Component comp = parentContainer.get(expression);
				if (comp == null) {
					return Collections.emptyList();
				} else {
					return Arrays.asList(comp);
				}
			}
		} else {
			return Collections.emptyList();
		}
	}

	private static List<Component> getAllChildren(MarkupContainer parent) {
		List<Component> children = new ArrayList<Component>();
		Iterator<? extends Component> iter = parent.iterator();
		while (iter.hasNext()) {
			children.add(iter.next());
		}
		return children;
	}

}
