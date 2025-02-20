import { defineConfig } from 'vite';
import eslintPlugin from "@nabla/vite-plugin-eslint";
import path from 'path';
import fs from 'fs';

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
    return {
        name: 'export-routes',
        generateBundle(options, bundle) {
            const outputDirectory = options.dir || 'dist';
            const assetsDirectory = path.join(outputDirectory, 'assets');
            const filePath = path.join(assetsDirectory, 'routes.txt');
            const fileContent = 'Written by vite';
            fs.writeFileSync(filePath, fileContent);
        },
    };
};
