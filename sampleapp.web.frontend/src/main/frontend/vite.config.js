import { defineConfig } from 'vite';
import eslintPlugin from "@nabla/vite-plugin-eslint";

export default defineConfig({
    plugins: [eslintPlugin(), exportRoutesPlugin],
    build: {
        minify: false,
        sourcemap: true,
        manifest: true,
        rollupOptions: {
            // overwrite default .html entry
            input: 'src/index.js',
            output: {
                entryFileNames: `assets/[name].js`,
                chunkFileNames: `assets/[name].js`,
                assetFileNames: `assets/[name].[ext]`
            }
        },
        // Relative to the root
        outDir: '../../../target/classes',
    },
    // Treat .js files as jsx
    esbuild: {
        include: /\.js$/,
        exclude: [],
        loader: 'jsx',
    },
});

const exportRoutesPlugin = {
  name: 'export-routes',
  enforce: 'post',
  async generateBundle(_, bundle) {
    const routePaths = new Set();

    const findRoutes = (code) => {
      const ast = parse(code, {
        sourceType: 'module',
        plugins: ['jsx'],
      });

      const routes = [];
      const traverseNode = (node) => {
        if (node.type === 'JSXElement' && node.openingElement.name.name === 'Route') {
          const pathAttr = node.openingElement.attributes.find(
            (attr) => attr.name.name === 'path'
          );
          if (pathAttr && pathAttr.value) {
            const pathValue = pathAttr.value.value;
            routes.push(pathValue);
          }
        }

        // Traverse children of the node
        if (node.children) {
          node.children.forEach((child) => traverseNode(child));
        }
      };

      ast.program.body.forEach((node) => traverseNode(node));
      return routes;
    };

    // Traverse all the files in the bundle and extract routes from React components
    for (const fileName in bundle) {
      const file = bundle[fileName];
      if (file.type === 'chunk' && file.code) {
        const code = transformSync(file.code, { loader: 'jsx' }).code;
        const routes = findRoutes(code);

        routes.forEach((route) => routePaths.add(route));
      }
    }

    const routesFilePath = path.resolve(__dirname, 'target/classes/routes.json');
    const routesArray = Array.from(routePaths);

    fs.writeFileSync(routesFilePath, JSON.stringify(routesArray, null, 2));

    console.log(`Routes have been exported to target/classes/routes.json`);
  },
};
