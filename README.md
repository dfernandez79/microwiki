Introduction
========

Microwiki is a minimalist wiki server to be used to add
documentation to software projects.

Some of the characteristics of microwiki are:

* No databases, pages are stored in files using [Markdown][1] format, and you
   can edit them directly using a text editor or your favorite IDE.

* Does not provide versioning: pages are stored in plain files, that let's you use
   SVN, GIT or your favorite SCM system.

*  No configuration needed: start microwiki in your document folder and you are
   done.



Roadmap
=======

This is the prioritized backlog of things to do for the 1.0 release:

1. Search functionality

2. Executable package
  *  [OneJar][2]

3. Zero configuration startup
   * When microwiki is started show the README.md in the current directory

   * If no README.md is found look for a docs sub-directory and show the
     index.md file in it

   * Remove the command line arguments and use a configuration file

      * Detect if the configuration file is present in the current directory and use
        it by default

4. Better directory index
   * Display only .md, .html and image files

      * For .md files display a teasser of the file contents

   * Allow to seach for files

   * Allow to create sub-directories

5. Update the license information
   * The project is going to use Apache v2 license
     * Put the required license messages
     * Put recognition of other used files/projects: Droid fonts, PegDown parser, Groovy, Jetty

Some things that I consider nice to have for a later release are (in no particular
order):

* Side bar (like Google Docs Wiki)

* Print page, with custom stylesheet to make the pages look nice when printed

  * URLs should be displayed as footnotes

  * Show a warning to the user to not spent unecessary paper

* Edit warning (if you change something that was already changed, the wiki should
  show a warning, and it will be cool if it allows you to do a merge)

* Shortcuts in the edit page, ie: Ctrl-S to Save

* Integration with Ant/Gradle/Maven

* Extension to allow the use of LaTeX expressions in code blocks

* Extension to allow UML and GrapViz files

  *  For example image links to files with .uml.svg or .uml.png are handled by a
     servlet that renders the UML from a text source (probably using [PlantUml][3])

* Preview in the edit page

* MacOSX integration
  * Bundle the JAR in a Mac application bundle
  * When started from app bundle show a UI to choose the docroot directory
  * Allow to control the server from the UI (start/stop/re-index files)

* Windows integration
  * Same startup UI as MacOSX but use a system tray icon

* IDE integration (Eclipse/IDEA/NetBeans)
   * Right click in a project to show the documentation, starts the server and shows
     the internal eclipse web browser to display the docs

   * Links to classes displays the "Open Type" functionality in Eclipse

[1]: http://daringfireball.net/projects/markdown/
[2]: http://one-jar.sourceforge.net/
[3]: http://plantuml.sourceforge.net/