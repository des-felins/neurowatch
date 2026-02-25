import { injectGlobalWebcomponentCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';

import $cssFromFile_0 from '@vaadin/vaadin-lumo-styles/lumo.css?inline';

injectGlobalWebcomponentCss($cssFromFile_0.toString());
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/grid/src/vaadin-grid.js';
import '@vaadin/grid/src/vaadin-grid-column.js';
import '@vaadin/grid/src/vaadin-grid-sorter.js';
import '@vaadin/checkbox/src/vaadin-checkbox.js';
import 'Frontend/generated/jar-resources/flow-component-renderer.js';
import 'Frontend/generated/jar-resources/flow-component-directive.js';
import 'lit';
import 'Frontend/generated/jar-resources/gridConnector.ts';
import '@vaadin/component-base/src/debounce.js';
import '@vaadin/component-base/src/async.js';
import '@vaadin/grid/src/vaadin-grid-active-item-mixin.js';
import 'Frontend/generated/jar-resources/vaadin-grid-flow-selection-column.js';
import '@vaadin/tooltip/src/vaadin-tooltip.js';
import '@vaadin/grid/src/vaadin-grid-column-group.js';
import 'Frontend/generated/jar-resources/lit-renderer.ts';
import 'lit/directives/live.js';
import '@vaadin/context-menu/src/vaadin-context-menu.js';
import 'Frontend/generated/jar-resources/contextMenuConnector.js';
import 'Frontend/generated/jar-resources/contextMenuTargetConnector.js';
import '@vaadin/component-base/src/gestures.js';
import 'Frontend/generated/jar-resources/disableOnClickFunctions.js';
import '@vaadin/integer-field/src/vaadin-integer-field.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/dialog/src/vaadin-dialog.js';
import '@vaadin/tabs/src/vaadin-tab.js';
import '@vaadin/tabs/src/vaadin-tabs.js';
import '@vaadin/date-picker/src/vaadin-date-picker.js';
import 'Frontend/generated/jar-resources/datepickerConnector.js';
import 'date-fns/parse';
import '@vaadin/date-picker/src/vaadin-date-picker-helper.js';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/form-layout/src/vaadin-form-item.js';
import '@vaadin/form-layout/src/vaadin-form-row.js';
import '@vaadin/login/src/vaadin-login-form.js';
import '@vaadin/app-layout/src/vaadin-app-layout.js';
import '@vaadin/app-layout/src/vaadin-drawer-toggle.js';
import '@vaadin/text-area/src/vaadin-text-area.js';
import '@vaadin/notification/src/vaadin-notification.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';
import 'react-router';
import 'react';
const loadOnDemand = (key) => {
  const pending = [];
  if (key === '32f2bc06a6c53941efa3aebc4403b53b967e25996cb6604c020b74b4143c3f23') {
    pending.push(import('./chunks/chunk-e552e36da171d02ff065802fc38892db987b87b45f09d0bf3ec89ed6ff44bf66.js'));
  }
  if (key === 'e8c5054888c7271d170553d60b2ab51fa9e7f29f3fdac3a6ced861e001c77bbf') {
    pending.push(import('./chunks/chunk-3f44bb5b234d4e52365b09b41b4b0bb4d4856ea5560b30e4c066013aa264da08.js'));
  }
  return Promise.all(pending);
}
window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}