var gulp = require('gulp');
const sourcemaps = require('gulp-sourcemaps');
const babel = require('gulp-babel');
var sass = require('gulp-sass');
var inject = require('gulp-inject');
var clean = require('gulp-clean');
var gnf = require('gulp-npm-files');

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
             .pipe(gulp.dest(destination + 'javascript'));
});

gulp.task('vendor', function() {
  gulp.src(gnf(), {base:'./node_modules/'}).pipe(gulp.dest(destination + 'vendor'));
});

gulp.task('html', ['javascript', 'vendor', 'sass'], function() {
  var target = gulp.src('src/*.html');
  var sources = gulp.src([destination + 'javascript/**/*.js', destination + 'css/**/*.css'], {read: false});

  return target.pipe(inject(sources))
               .pipe(gulp.dest(destination));
});

gulp.task('clean', function () {
  return gulp.src('dist/*', {read: false})
             .pipe(clean());
});

gulp.task('watch', function () {
  gulp.watch('src/javascript/**/*.js', ['html']);
  gulp.watch('src/scss/**/*.scss', ['html']);
});

gulp.task('default', ['html']);
