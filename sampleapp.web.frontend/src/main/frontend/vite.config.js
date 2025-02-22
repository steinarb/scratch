import { defineConfig } from 'vite';
import eslintPlugin from "@nabla/vite-plugin-eslint";
import path from 'path';
import fs from 'fs';
import { parse } from '@babel/parser';
import traverse from '@babel/traverse';
import * as t from "@babel/types";

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
                const ast = parse(src, {
                    sourceType: 'module',
                    plugins: ['jsx'],
                });

                traverse(ast, {
                    enter(path) {
                        if (t.isJSXElement(path.node)) {
                            console.log('JSX Element:', path.node);
                        }
                    }
                });
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
