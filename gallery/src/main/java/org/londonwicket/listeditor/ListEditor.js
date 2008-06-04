function createSortable(element, callbackUrl) {
	Sortable.destroy(element);
	Sortable.create(element, {
		handle: 'handle',
		dropOnEmpty: true,
		constraint: false,
		tag: 'div',
		onUpdate: function(element) {
			wicketAjaxGet(callbackUrl + "&" + Sortable.serialize(element));
		}
	} );
}
