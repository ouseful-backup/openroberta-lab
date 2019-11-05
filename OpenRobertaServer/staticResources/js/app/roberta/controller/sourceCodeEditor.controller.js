define([ 'require', 'exports', 'message', 'log', 'util', 'comm', 'guiState.controller', 'program.model', 'program.controller', 'progRun.controller','import.controller', 'blocks', 'jquery', 'blocks-msg'  ], 
        function(require, exports, MSG, LOG, UTIL, COMM, GUISTATE_C, PROGRAM, PROG_C, PROGRUN_C, IMPORT_C,Blockly, $) {

    function init() {
        $("#sourceCodeEditorTextArea").attr("placeholder", "Import code from Blockly workspace or just start typing");
        initEvents();
    }
    exports.init = init;

    function initEvents() {
        $('#backSourceCodeEditor').onWrap('click', function() {
            $('#' + GUISTATE_C.getPrevView()).trigger('click');
            return false;
        }, "back to previous view");
        
        $('#runSourceCodeEditor').onWrap('click', function() {
            PROGRAM.runNative(GUISTATE_C.getProgramName(), $("#sourceCodeEditorTextArea").val(), GUISTATE_C.getLanguage(), function(result) {
                if (result.rc == "ok") {
                    PROGRUN_C.runOnBrick();
                } else {
                    MSG.displayInformation(result, result.message, result.message, GUISTATE_C.getProgramName());
                }
            });
            return false;
        }, "run button clicked");
        
        $('#buildSourceCodeEditor').onWrap('click', function() {
            PROGRAM.compileN(GUISTATE_C.getProgramName(), $("#sourceCodeEditorTextArea").val(), GUISTATE_C.getLanguage(), function(result) {
                if (result.rc == "ok") {
                    MSG.displayPopupMessage(result.message, result.message, false);
                } else {
                    MSG.displayInformation(result, result.message, result.message, GUISTATE_C.getProgramName());
                }
            });
            return false;
        }, "build button clicked");
        
        $('#downloadSourceCodeEditor').onWrap('click', function() {
            var filename = GUISTATE_C.getProgramName() + '.' + GUISTATE_C.getSourceCodeFileExtension();
            UTIL.download(filename, $("#sourceCodeEditorTextArea").val());
            MSG.displayMessage("MENU_MESSAGE_DOWNLOAD", "TOAST", filename);
            return false;
        }, "download source code button clicked");
        
        $('#uploadSourceCodeEditor').onWrap('click', function() {
            IMPORT_C.importSourceCode(function(name, source) {
                $("#sourceCodeEditorTextArea").val(source);
            });
            return false;
        }, "upload source code button clicked");
        
        $('#importSourceCodeEditor').onWrap('click', function() {
            getSourceCode();
            return false;
        }, "import from blockly button clicked");
    }
    
    function getSourceCode() {
        var blocklyWorkspace = GUISTATE_C.getBlocklyWorkspace();
        var dom = Blockly.Xml.workspaceToDom(blocklyWorkspace);
        var xmlProgram = Blockly.Xml.domToText(dom);
        var isNamedConfig = !GUISTATE_C.isConfigurationStandard() && !GUISTATE_C.isConfigurationAnonymous();
        var configName = isNamedConfig ? GUISTATE_C.getConfigurationName() : undefined;
        var xmlConfigText = GUISTATE_C.isConfigurationAnonymous() ? GUISTATE_C.getConfigurationXML() : undefined;
        var language = GUISTATE_C.getLanguage();
        PROGRAM.showSourceProgram(GUISTATE_C.getProgramName(), configName, xmlProgram, xmlConfigText, PROG_C.SSID, PROG_C.password, language, 
                function(result) {
            if (result.rc == "ok") {
                $("#sourceCodeEditorTextArea").val(result.sourceCode);
            } else {
                MSG.displayInformation(result, result.message, result.message, result.parameters);
            }
        });
    }
});
