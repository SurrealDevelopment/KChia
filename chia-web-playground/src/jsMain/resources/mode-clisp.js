ace.define("ace/mode/clisp_highlight_rules",["require","exports","module","ace/lib/oop","ace/mode/text_highlight_rules"], function(require, exports, module) {
    "use strict";

    var oop = require("../lib/oop");
    var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;

    var LispHighlightRules = function() {
        var keywordControl = "i|if";
        var keywordOperator = "=|eq|+|add|-|subtract|*|multiply|divmod|/|div|>|gr|>s|gr_bytes|ash|lsh|not|any|all";
        var constantLanguage = "()";
        var supportFunctions = "swap|x|raise|sha256|pubkey_for_exp|point_add|strlen|substr|concat|logand|logior|" +
            "logxor|lognot|softfork|" +
            "c|cons|f|first|r|rest|l|listp|" +
            "mod|list" + // HIGH LEVEL
            "";
        var keywordMapper = this.createKeywordMapper({
            "keyword.control": keywordControl,
            "keyword.operator": keywordOperator,
            "constant.language": constantLanguage,
            "support.function": supportFunctions
        }, "identifier", true);

        this.$rules =
            {
                "start": [
                    {
                        token: "comment",
                        regex: ";.*$"
                    },
                    {
                        token: ["storage.type.function-type.lisp", "text", "entity.name.function.lisp"],
                        regex: "(?:\\b(?:(defun|defmacro|defconstant))\\b)(\\s+)((?:\\w|\\-|\\!|\\?)*)"
                    },
                    {
                        token: ["punctuation.definition.constant.character.lisp", "constant.character.lisp"],
                        regex: "(#)((?:\\w|[\\\\+-=<>'\"&#])+)"
                    },
                    {
                        token: ["punctuation.definition.variable.lisp", "variable.other.global.lisp", "punctuation.definition.variable.lisp"],
                        regex: "(\\*)(\\S*)(\\*)"
                    },
                    {
                        token: "constant.numeric", // hex
                        regex: "0[xX][0-9a-fA-F]+(?:L|l|UL|ul|u|U|F|f|ll|LL|ull|ULL)?\\b"
                    },
                    {
                        token: keywordMapper,
                        regex: "[a-zA-Z_$][a-zA-Z0-9_$]*\\b"
                    },
                    {
                        token: "string",
                        regex: '"(?=.)',
                        next: "qqstring"
                    }
                ],
                "qqstring": [
                    {
                        token: "constant.character.escape.lisp",
                        regex: "\\\\."
                    },
                    {
                        token: "string",
                        regex: '[^"\\\\]+'
                    }, {
                        token: "string",
                        regex: "\\\\$",
                        next: "qqstring"
                    }, {
                        token: "string",
                        regex: '"|$',
                        next: "start"
                    }
                ]
            };

    };

    oop.inherits(LispHighlightRules, TextHighlightRules);

    exports.LispHighlightRules = LispHighlightRules;
});

ace.define("ace/mode/clisp", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text", "ace/mode/clisp_highlight_rules"], function (require, exports, module) {
    "use strict";

    const oop = require("../lib/oop");
    const TextMode = require("./text").Mode;
    const LispHighlightRules = require("./clisp_highlight_rules").LispHighlightRules;

    const Mode = function () {
        this.HighlightRules = LispHighlightRules;
        this.$behaviour = this.$defaultBehaviour;
    };
    oop.inherits(Mode, TextMode);

    (function () {

        this.lineCommentStart = ";";

        this.$id = "ace/mode/clisp";
    }).call(Mode.prototype);

    exports.Mode = Mode;
});
(function () {
    ace.require(["ace/mode/clisp"], function (m) {
        if (typeof module == "object" && typeof exports == "object" && module) {
            module.exports = m;
        }
    });
})();
