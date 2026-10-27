{{flutter_js}}
{{flutter_build_config}}

// Append cache-busting timestamp parameter to entrypoints so the browser never serves stale JS
if (_flutter && _flutter.buildConfig && Array.isArray(_flutter.buildConfig.builds)) {
  _flutter.buildConfig.builds.forEach(function (build) {
    if (build && typeof build.mainJsPath === 'string') {
      build.mainJsPath = build.mainJsPath + (build.mainJsPath.indexOf('?') >= 0 ? '&' : '?') + 'v=' + new Date().getTime();
    }
  });
}

_flutter.loader.load({
  // Omitting serviceWorkerSettings disables Flutter's service worker completely
  onEntrypointLoaded: async function (engineInitializer) {
    const appRunner = await engineInitializer.initializeEngine();
    await appRunner.runApp();
  }
});
