/**
 * @license Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
	config.height = 80; 
	config.toolbarCanCollapse = true;
	config.toolbarStartupExpanded = false;
	config.toolbar = [
		              	{ name: 'document', groups: [ 'mode', 'document', 'doctools' ], items: [ 'Source', '-', 'Preview' ] },
		              	{ name: 'clipboard', groups: [ 'clipboard', 'undo' ], items: [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ] },
		              	{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ], items: [ 'Find', 'Replace', '-', 'SelectAll', '-', 'Scayt' ] },
		              	{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', 'Strike',  '-', 'RemoveFormat' ] },
		            	{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items: [ 'NumberedList', 'BulletedList'] },
		            	'/',
		              	{ name: 'links', items: [ 'Link', 'Unlink', 'Anchor' ] },
		              	{ name: 'insert', items: [  'Table', 'SpecialChar' ] },
		              	{ name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] },
		              	{ name: 'colors', items: [ 'TextColor' ] },
		              	{ name: 'tools', items: [ 'Maximize', 'ShowBlocks' ] }
		              	
		              ];

		              // Toolbar groups configuration.
		              config.toolbarGroups = [
		              	{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
		              	{ name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
		              	{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ] },
		              	{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
		             	{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ] },
		             	'/',
		              	{ name: 'links' },
		              	{ name: 'insert' },
		              	{ name: 'styles' },
		              	{ name: 'colors' },
		              	{ name: 'tools' }
		              
		              ];
	
	
};
