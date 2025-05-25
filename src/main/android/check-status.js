#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

console.log('🔍 Discord Bot Manager - Estado del APK');
console.log('=====================================');

// Verificar archivos de configuración
const configFiles = [
    'capacitor.config.js',
    'package.json',
    'www/index.html'
];

console.log('\n📋 Archivos de configuración:');
configFiles.forEach(file => {
    const exists = fs.existsSync(file);
    console.log(`${exists ? '✅' : '❌'} ${file}`);
});

// Verificar directorio Android
const androidPath = path.join(__dirname, 'android');
const androidExists = fs.existsSync(androidPath);
console.log(`\n📱 Proyecto Android: ${androidExists ? '✅ Existe' : '❌ No existe'}`);

if (androidExists) {
    // Verificar estructura Android
    const androidFiles = [
        'android/gradlew.bat',
        'android/app/build.gradle',
        'android/app/src/main/AndroidManifest.xml'
    ];
    
    console.log('\n🔧 Archivos Android:');
    androidFiles.forEach(file => {
        const exists = fs.existsSync(file);
        console.log(`${exists ? '✅' : '❌'} ${file}`);
    });
}

// Verificar APKs generados
const apkPaths = [
    'android/app/build/outputs/apk/debug/app-debug.apk',
    'android/app/build/outputs/apk/release/app-release.apk'
];

console.log('\n📦 APKs generados:');
apkPaths.forEach(apkPath => {
    const exists = fs.existsSync(apkPath);
    if (exists) {
        const stats = fs.statSync(apkPath);
        const sizeMB = (stats.size / 1024 / 1024).toFixed(2);
        const modified = stats.mtime.toLocaleString();
        console.log(`✅ ${apkPath}`);
        console.log(`   📏 Tamaño: ${sizeMB} MB`);
        console.log(`   🕒 Modificado: ${modified}`);
    } else {
        console.log(`❌ ${apkPath}`);
    }
});

// Verificar archivos estáticos sincronizados
const staticPath = path.join(__dirname, 'www', 'static');
const staticExists = fs.existsSync(staticPath);
console.log(`\n📁 Archivos estáticos: ${staticExists ? '✅ Sincronizados' : '❌ No sincronizados'}`);

if (staticExists) {
    const files = fs.readdirSync(staticPath);
    console.log(`   📊 Total de archivos: ${files.length}`);
}

// Mostrar comandos útiles
console.log('\n🚀 Comandos útiles:');
console.log('npm run copy          # Sincronizar archivos del backend');
console.log('npm run sync          # Sincronizar Capacitor');
console.log('npm run build         # Preparar proyecto');
console.log('npm run build-apk-debug    # Generar APK debug');
console.log('npm run open          # Abrir Android Studio');
console.log('npm run doctor        # Verificar configuración');

console.log('\n✨ Estado verificado');
