import './vaadin-featureflags.js';

import 'Frontend/generated/jar-resources/copilot.js';
// @ts-ignore
if (import.meta.hot) {
  // @ts-ignore
  import.meta.hot.on('vite:beforeUpdate', (e:any) => {
    if ((window as any).Vaadin.copilot?.disableViteHmr) {
        e.updates = [];
    }
  });
  // @ts-ignore
  import.meta.hot.on('vite:beforeFullReload', (payload:any) => {
    if ((window as any).Vaadin.copilot?.disableViteHmr) {
        payload.path = "something-not-used-in-the-app-to-prevent-reload.html";
    }
  });
  // @ts-ignore
  import.meta.hot.on('vite:afterUpdate', () => {
    const eventbus = (window as any).Vaadin.copilot.eventbus;
    if (eventbus) {
      eventbus.emit('vite-after-update',{});
    }
  });
}

import '@vaadin/vertical-layout/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/vaadin-horizontal-layout.js';
import '@vaadin/context-menu/vaadin-context-menu.js';
import '@vaadin/checkbox/vaadin-checkbox.js';
import '@vaadin/text-field/vaadin-text-field.js';
import '@vaadin/text-area/vaadin-text-area.js';
import '@vaadin/menu-bar/vaadin-menu-bar.js';
import '@vaadin/grid/vaadin-grid.js';
import '@vaadin/grid/vaadin-grid-tree-column.js';
import '@vaadin/details/vaadin-details.js';
import '@vaadin/select/vaadin-select.js';
import '@vaadin/overlay/vaadin-overlay.js';
import '@vaadin/popover/vaadin-popover.js';
import '@vaadin/list-box/vaadin-list-box.js';
import '@vaadin/combo-box/vaadin-combo-box.js';
import '@vaadin/item/vaadin-item.js';
import '@vaadin/tabsheet/vaadin-tabsheet.js';
import '@vaadin/dialog/vaadin-dialog.js';
import '@vaadin/multi-select-combo-box/vaadin-multi-select-combo-box.js';
import '@vaadin/radio-group/vaadin-radio-group.js';
import '@vaadin/tooltip/vaadin-tooltip.js';
import '@vaadin/icons/vaadin-iconset.js';
import '@vaadin/icon/vaadin-icon.js';
import './index';

import './vaadin-react.js';
import 'Frontend/generated/jar-resources/vaadin-dev-tools/vaadin-dev-tools.js';

import './app-shell-imports.js';
import './theme-cyber.global.generated.js';
import { applyTheme } from './theme.js';
applyTheme(document);

import { Outlet } from 'react-router';
(window as any).Vaadin ??= {};
(window as any).Vaadin.copilot ??= {};
(window as any).Vaadin.copilot._ref ??= {};
(window as any).Vaadin.copilot._ref.Outlet = Outlet;
