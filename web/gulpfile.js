var gulp = require('gulp');
const sourcemaps = require('gulp-sourcemaps');
const babel = require('gulp-babel');
var sass = require('gulp-sass');
var inject = require('gulp-inject');
var clean = require('gulp-clean');
var gnf = require('gulp-npm-files');
var gutil = require('gulp-util');
var webpack = require('webpack');
var webpackConfig = require('./webpack.config');

var destination = 'dist/web/';

gulp.task('sass', function () {
  return gulp.src('src/scss/**/*.scss')
             .pipe(sourcemaps.init())
             .pipe(sass().on('error', sass.logError))
             .pipe(sourcemaps.write())
             .pipe(gulp.dest(destination + 'css'));
});

gulp.task('javascript', function() {
  return gulp.src('src/javascript/**/*.js')
             .pipe(sourcemaps.init())
             .pipe(babel({
               plugins: ['transform-runtime'],
               presets: ['es2015']
             }))
             .pipe(sourcemaps.write('.'))
             .pipe(gulp.dest('tmp'));
});

gulp.task('webpack', ['javascript'], function(callback) {
    var myConfig = Object.create(webpackConfig);

    // run webpack
    webpack(myConfig, function(err, stats) {
        if (err) throw new gutil.PluginError('webpack', err);
        gutil.log('[webpack]', stats.toString({
            colors: true,
            progress: true
        }));
        callback();
    });
});

gulp.task('vendor', function() {
    gulp.src(gnf(), {base:'./node_modules/'}).pipe(gulp.dest(destination + 'vendor'));
});

gulp.task('html', ['webpack', 'vendor', 'sass'], function() {
    var target = gulp.src('src/*.html');
    var sources = gulp.src([destination + 'javascript/**/*.js', destination + 'css/**/*.css'], {read: false});

    return target.pipe(inject(sources))
        .pipe(gulp.dest(destination));
});

gulp.task('clean', function () {
    return gulp.src('{dist,tmp}/*', {read: false})
        .pipe(clean());
});

gulp.task('watch', ['html'], function () {
    gulp.watch('src/javascript/**/*.js', ['html']);
    gulp.watch('src/scss/**/*.scss', ['html']);
    gulp.watch('src/*.html', ['html']);
});

gulp.task('default', ['html']);
