var gulp = require('gulp');
var mainBowerFiles = require('main-bower-files');
var uglify = require('gulp-uglify');
var cleanCSS = require('gulp-clean-css')
var runSequence = require('run-sequence');
var rename = require('gulp-rename');
var sourcemaps = require('gulp-sourcemaps');

var vendorPath = 'target/classes/web/module/resources/vendor';

// Copy javascript files to vendor folder and uglify them
gulp.task('lib-js-files', function() {
    gulp.src(mainBowerFiles('**/*.js'), {
            base: 'bower_components'
        })
        .pipe(uglify())
        .pipe(rename({extname: '.min.js'}))
        .pipe(gulp.dest(vendorPath));
});

// Copy css files to vendor folder and minify them
gulp.task('lib-css-files', function() {
    gulp.src(mainBowerFiles('**/*.css'), {
            base: 'bower_components'
        })
        .pipe(cleanCSS())
        .pipe(rename({extname: '.min.css'}))
        .pipe(gulp.dest(vendorPath));
    gulp.src('bower_components/tinymce/skins/lightgray/*.min.css')
        .pipe(gulp.dest(vendorPath+'/tinymce/skins/lightgray'));
});

// Copy javascript files to vendor folder, uglify them and create sourcemaps
gulp.task('lib-js-files-with-sourcemaps', function() {
    gulp.src(mainBowerFiles('**/*.js'), {
            base: 'bower_components'
        })
        .pipe(sourcemaps.init())
        .pipe(uglify())
        .pipe(rename({extname: '.min.js'}))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(vendorPath));
});

// Copy css files to vendor folder, minify them and create sourcemaps
gulp.task('lib-css-files-with-sourcemaps', function() {
    gulp.src(mainBowerFiles('**/*.css'), {
            base: 'bower_components'
        })
        .pipe(sourcemaps.init())
        .pipe(cleanCSS())
        .pipe(rename({extname: '.min.css'}))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(vendorPath));
    gulp.src('bower_components/tinymce/skins/lightgray/*.min.css')
        .pipe(gulp.dest(vendorPath+'/tinymce/skins/lightgray'));
});

// Copy gif files to vendor folder
gulp.task('lib-gif-files', function() {
    gulp.src(mainBowerFiles('**/*.gif'), {
            base: 'bower_components'
        })
        .pipe(gulp.dest(vendorPath));
});

// Copy fonts to vendor folder
gulp.task('lib-font-files', function() {
    gulp.src(mainBowerFiles('**/*.{otf,eot,svg,ttf,woff,woff2}'), {
            base: 'bower_components'
        })
        .pipe(gulp.dest(vendorPath));
});

// Default Task
gulp.task('default', function() {
    runSequence('lib-js-files', 'lib-css-files', 'lib-font-files', 'lib-gif-files');
});

// Development Task
gulp.task('dev', function() {
    runSequence('lib-js-files-with-sourcemaps', 'lib-css-files-with-sourcemaps', 'lib-font-files', 'lib-gif-files');
});
