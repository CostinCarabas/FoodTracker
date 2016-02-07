module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    uglify: {
      options: {
        banner: '/*! server <%= grunt.template.today("yyyy-mm-dd") %> */\n'
      },
      build: {
        src: 'src/server.js',
        dest: 'build/server.min.js'
      }
    },
    jsdoc : {
    dist : {
            src: ['src/*.js'],
            options: {
                destination: 'doc'
            }
        }
    }
  });

  // Load the plugin that provides the "uglify" task.
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-jsdoc');

  // Default task(s).
  grunt.registerTask('default', ['uglify', 'jsdoc']);

};
