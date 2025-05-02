<template>
    <div ref="CodeMirror" class="code-contrast" style="width: 100%; height: 100%"></div>
</template>

<script>
import CodeMirror from "codemirror";
import "codemirror/lib/codemirror.css";
import "codemirror/addon/merge/merge.js";
import "codemirror/addon/merge/merge.css";
import DiffMatchPatch from "diff-match-patch";

// 将DiffMatchPatch暴露给全局变量
window.diff_match_patch = DiffMatchPatch;
window.DIFF_DELETE = -1;
window.DIFF_INSERT = 1;
window.DIFF_EQUAL = 0;

export default {
    name: 'CodeMirror',
    props: {
        oldValue: {
            type: String,
            default: "",
        },
        newValue: {
            type: String,
            default: "",
        },
        isReadOnly: {
            type: Boolean,
            default: true,
        },
    },
    data() {
        return {};
    },
    methods: {
        initCodeMirrorUI() {
            if (this.newValue == null) return;
            let target = this.$refs.CodeMirror;
            target.innerHTML = "";

            CodeMirror.MergeView(target, {
                value: this.oldValue, // 上次内容
                origLeft: null,
                orig: this.newValue, // 本次内容
                lineNumbers: true, // 显示行号
                mode: "text/html",
                highlightDifferences: true,
                connect: "align",
                readOnly: this.isReadOnly, // 只读 不可修改
            });
        },
    },
    mounted() {
        this.initCodeMirrorUI();
    },
};
</script>

<style scoped>
.code-contrast .CodeMirror-merge-copy,
.code-contrast .CodeMirror-me
rge-scrolllock-wrap {
    display: none !important;
}
</style>