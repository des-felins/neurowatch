import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';
import { webcomponentGlobalCssInjector } from 'Frontend/generated/jar-resources/theme-util.js';
import './theme-cyber.components.generated.js';
let needsReloadOnChanges = false;
import lumoUtil from '@vaadin/vaadin-lumo-styles/utility.css?inline';
import stylesCss from 'themes/cyber/styles.css?inline';
if(!document.getElementById("lumoUtil")) {
      const styleTag = document.createElement('style');
      styleTag.type = 'text/css';
      styleTag.id = 'lumoUtil';
      styleTag.textContent = lumoUtil.toString();
      document.head.prepend(styleTag);
    }
  let themeRemovers = new WeakMap();
  let targets = [];
  const fontFaceRegex = /(@font-face\s*{[\s\S]*?})/g;

  export const applyTheme = (target) => {
    const removers = [];
    if (target !== document) {
      removers.push(injectGlobalCss(lumoUtil.toString(), '', target));
    removers.push(injectGlobalCss(stylesCss.toString(), '', target));
    
      
        webcomponentGlobalCssInjector((css) => {
          removers.push(injectGlobalCss(css, '', target));
          if(fontFaceRegex.test(css)) {
            const fontFaces = Array.from(css.match(fontFaceRegex));
            fontFaces.forEach(fontFace => {
              removers.push(injectGlobalCss(fontFace, '', document));
            });
          }
        });
        
    }
    
    

    if (import.meta.hot) {
      targets.push(new WeakRef(target));
      themeRemovers.set(target, removers);
    }

  }


if (import.meta.hot) {
  import.meta.hot.accept((module) => {

    if (needsReloadOnChanges) {
      window.location.reload();
    } else {
      targets.forEach(targetRef => {
        const target = targetRef.deref();
        if (target) {
          themeRemovers.get(target).forEach(remover => remover())
          module.applyTheme(target);
        }
      })
    }
  });

  import.meta.hot.on('vite:afterUpdate', (update) => {
    document.dispatchEvent(new CustomEvent('vaadin-theme-updated', { detail: update }));
  });
}

