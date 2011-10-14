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

[1]: http://daringfireball.net/projects/markdown/

Things to do
=======

These are some things to do (in no particular order). I'm going to implement them
as my time allows it, but if you want to help... do a fork and send patches, they are
welcome.

* Search functionality

* Edit warning (if you change something that was already changed, the wiki should
  show a warning, and it will be cool if it allows you to do a merge)

* Print page

* Executable package

* Automatic recognition of docs sub-folders

* Side bar (like Google Docs Wiki)

* A nice directory listing page (instead of the default used by Jetty)

 * Redirect of / to /index.md

 * Integration with Ant/Gradle/Maven

 * Extension to allow the use of LaTeX expressions in code blocks

 * Shortcuts in the edit page

 * Preview in the edit page