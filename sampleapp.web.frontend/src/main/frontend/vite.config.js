import { defineConfig } from 'vite';
import eslintPlugin from "@nabla/vite-plugin-eslint";
import path from 'path';
import fs from 'fs';
import { parse } from '@babel/core';

export default defineConfig({
    plugins: [eslintPlugin(), exportRoutesPlugin()],
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

function exportRoutesPlugin() {
    const routePaths = new Set();

    return {
        name: 'export-routes',

        async transform(src, id) {
            if (!id.includes('node_modules') && id.includes('.js')) {
                console.log('hei(1)');
                console.log(id);
                const ast = parse(src, {
                });
                console.log('hei(2)');

                const findPaths = (node) => {
                    if (node.type === 'JSXElement' && node.openingElement.name.name === 'Route') {
                        const pathAttr = node.openingElement.attributes.find(
                            (attr) => attr.name.name === 'path'
                        );
                        if (pathAttr && pathAttr.value) {
                            const pathValue = pathAttr.value.value;
                            routePaths.add(pathValue);
                        }
                    }
                    if (node.children) {
                        node.children.forEach((child) => findPaths(child));
                    }
                };
                ast.program.body.forEach((node) => findPaths(node));
            }
        },

        generateBundle(options, bundle) {
            const outputDirectory = options.dir || 'dist';
            const assetsDirectory = path.join(outputDirectory, 'assets');
            const filePath = path.join(assetsDirectory, 'routes.txt');
            const fileContent = Array.from(routePaths).join('\n');
            fs.writeFileSync(filePath, fileContent);
        },
    };
};
