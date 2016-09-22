var gulp = require('gulp');
const sourcemaps = require('gulp-sourcemaps');
const babel = require('gulp-babel');
var sass = require('gulp-sass');
var inject = require('gulp-inject');
var clean = require('gulp-clean');

gulp.task('sass', function () {
    return gulp.src('src/scss/**/*.scss')
        .pipe(sourcemaps.init())
        .pipe(sass().on('error', sass.logError))
        .pipe(sourcemaps.write())
        .pipe(gulp.dest('dist/css'));
});

gulp.task('javascript', function() {
    return gulp.src('src/javascript/**/*.js')
        .pipe(sourcemaps.init())
        .pipe(babel({
            plugins: ['transform-runtime'],
            presets: ['es2015']
        }))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest('dist/javascript'));
});

gulp.task('html', ['javascript', 'sass'], function() {
    var target = gulp.src('src/index.html');
    var sources = gulp.src(['./dist/javascript/**/*.js', './dist/css/**/*.css'], {read: false});

    return target.pipe(inject(sources))
        .pipe(gulp.dest('./dist'));
});

gulp.task('clean', function () {
    return gulp.src('dist/*', {read: false})
        .pipe(clean());
});

gulp.task('default', ['html']);
