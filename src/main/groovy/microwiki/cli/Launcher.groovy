package microwiki.cli

import java.awt.Desktop
import java.awt.Desktop.Action
import microwiki.Server
import microwiki.config.Config
import microwiki.config.dsl.ConfigBuilder
import microwiki.config.dsl.ConfigScriptException

class Launcher {
    static final LAUNCHER_PROGRAM = 'microwiki'
    static final VERSION = '1.0'
    static final DEFAULT_CONFIG_FILENAME = 'microwiki.conf'

    private static final Closure DEFAULT_DISPLAY_PAGE_ON_BROWSER_ACTION = { URI uri ->
        if (Desktop.desktopSupported && Desktop.desktop.isSupported(Action.BROWSE)) {
            Desktop.desktop.browse(uri)
        }
    }

    private static final List<String> WELCOME_FILES = ['README.md', 'index.md']

    private final String[] args
    private final Closure displayPageOnBrowserAction

    static void main(String[] args) {
        try {
            Server server = new Launcher(DEFAULT_DISPLAY_PAGE_ON_BROWSER_ACTION, args).startServer()

            if (server != null) {
                Runtime.runtime.addShutdownHook(new Thread({ server.stop() } as Runnable))
                server.join()
            }
        } catch (IllegalArgumentException e) {
            println "ERROR: $e.message"
        } catch (ConfigScriptException e) {
            println "ERROR: $e.message"
            println '\nError details:'
            e.cause.printStackTrace(System.out)
        }
    }

    Launcher(Closure displayPageOnBrowserAction, String... args) {
        this.args = args
        this.displayPageOnBrowserAction = displayPageOnBrowserAction
    }

    Server startServer() {
        CliBuilder commandLineParser = newCommandLineParser()
        def options = commandLineParser.parse(args)
        if (options == null) {
            return null
        } else if (options.help) {
            commandLineParser.usage()
            return null
        } else if (options.hasOption('config-example')) {
            print ConfigBuilder.CONFIG_SCRIPT_EXAMPLE
            return null
        }

        startServerWith(options)
    }

    private startServerWith(OptionAccessor options) {
        File docRoot = resolveDocRoot(options.arguments())
        Config config = readConfiguration(resolveConfigurationFile(docRoot, options))

        println "$LAUNCHER_PROGRAM - $VERSION"
        println "Document root: ${docRoot.absoluteFile.canonicalPath}"
        println config

        def server = new Server(docRoot, config)
        server.start()

        displayPageOnBrowser(docRoot, config.server.port)

        server
    }

    File resolveConfigurationFile(File docRoot, OptionAccessor options) {
        if (options.config) {
            File configFile = options.getOptionObject('config')
            if (!configFile.file) {
                configFile = new File(docRoot, options.getOptionValue('config'))
            }
            if (!(configFile.file && configFile.canRead())) {
                throw new IllegalArgumentException("'$configFile' is not a readable configuration file")
            }
            configFile
        } else {
            new File(docRoot, DEFAULT_CONFIG_FILENAME)
        }
    }

    private Config readConfiguration(File file) {
        if (file.file && file.canRead()) {
            file.withReader {
                Config.readFrom it
            }
        } else {
            new Config()
        }
    }

    File resolveDocRoot(List<String> args) {
        File docRoot = args.empty ? new File('.') : new File(args[0]);
        if (!(docRoot.directory && docRoot.canRead())) {
            throw new IllegalArgumentException("$docRoot is not an existing or readable directory")
        }
        docRoot
    }

    boolean anyWelcomeFileIn(File currentDir) {
        WELCOME_FILES.any { new File(currentDir, it).file }
    }

    private displayPageOnBrowser(File docRoot, int port) {
        def found = WELCOME_FILES.find { new File(docRoot, it).file }
        if (found == null) {
            found = WELCOME_FILES.find { new File(docRoot, "docs/$it").file }
        }
        if (found != null) {
            displayPageOnBrowserAction.call(new URI("http://localhost:$port/$found"))
        }
    }

    private CliBuilder newCommandLineParser() {
        def cli = new CliBuilder(usage: "$LAUNCHER_PROGRAM [options] <docroot>", header: 'options:')
        cli.with {
            c(longOpt: 'config', args: 1, argName: 'configFile', type: File.class,
                    'Uses the specified config file. When not specified the '
                            + "application will look for ${DEFAULT_CONFIG_FILENAME} in "
                            + 'the document root.')
            _(longOpt: 'config-example',
                    'Outputs a config file example into the console and exits.')
            _(longOpt: 'help', 'Displays this message and exits.')
        }
        cli
    }
}