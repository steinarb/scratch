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
    const files = [];

    return {
        name: 'export-routes',

        async transform(src, id) {
            if (!id.includes('node_modules')) {
                files.push(id);
            }
        },

        generateBundle(options, bundle) {
            const outputDirectory = options.dir || 'dist';
            const assetsDirectory = path.join(outputDirectory, 'assets');
            const filePath = path.join(assetsDirectory, 'routes.txt');
            const fileContent = files.join('\n');
            fs.writeFileSync(filePath, fileContent);
        },
    };
};
