const gulp = require("gulp");
const sourcemaps = require("gulp-sourcemaps");
const plumber = require("gulp-plumber");
const sass = require("gulp-sass");
const inject = require("gulp-inject");
const clean = require("gulp-clean");
const gnf = require("gulp-npm-files");
const gutil = require("gulp-util");
const webpack = require("webpack");
const webpackConfig = require("./webpack.config");

const destination = "dist/web/";

gulp.task("sass", function () {
  return gulp.src("src/scss/**/*.scss")
             .pipe(plumber(function (error) {
               gutil.log(error.message);
               this.emit("end");
             }))
             .pipe(sourcemaps.init({ loadMaps: true }))
             .pipe(sass().on("error", sass.logError))
             .pipe(sourcemaps.write())
             .pipe(gulp.dest(destination + "css"));
});

gulp.task("webpack", function(callback) {
  const myConfig = Object.create(webpackConfig);

  // run webpack
  webpack(myConfig, function(err, stats) {
    if (err) throw new gutil.PluginError("webpack", err);
    gutil.log("[webpack]", stats.toString({
      colors: true,
      progress: true
    }));
    callback();
  });
});

gulp.task("vendor", function() {
  gulp.src(gnf(), {base:"./node_modules/"}).pipe(gulp.dest(destination + "vendor"));
});

gulp.task("html", ["webpack", "vendor", "sass"], function() {
  const target = gulp.src("src/*.html");
  const sources = gulp.src([destination + "javascript/**/*.js", destination + "css/**/*.css"], {read: false});

  return target
    .pipe(plumber(function (error) {
      gutil.log(error.message);
      this.emit("end");
    }))
    .pipe(inject(sources))
    .pipe(gulp.dest(destination));
});

gulp.task("clean", function () {
  return gulp.src("dist/*", {read: false})
             .pipe(clean());
});

gulp.task("watch", ["html"], function () {
  gulp.watch("src/javascript/**/*.js", ["html"]);
  gulp.watch("src/scss/**/*.scss", ["html"]);
  gulp.watch("src/*.html", ["html"]);
});

gulp.task("default", ["html"]);
