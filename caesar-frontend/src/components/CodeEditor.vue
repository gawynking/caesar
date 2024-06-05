<template>
    <div class="code-editor">
        <codemirror ref="editor" v-model="code" :options="editorOptions">
        </codemirror>
    </div>
</template>

<script>
import {codemirror} from 'vue-codemirror';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/material.css'; // 你可以选择其他主题
import 'codemirror/mode/javascript/javascript.js'; // 你可以选择其他语言模式

export default {
    components: { codemirror },
    props: {
        value: {
            type: String,
            default: ''
        }
    },
    data() {
        return {
            code: this.value,
            editorOptions: {
                mode: 'javascript', // 选择代码语言模式
                theme: 'material',  // 选择主题
                lineNumbers: true,  // 显示行号
                tabSize: 2,
                indentWithTabs: true
            }
        };
    },
    watch: {
        value(newVal) {
            if (newVal !== this.code) {
                this.code = newVal;
            }
        },
        code(newVal) {
            this.$emit('input', newVal);
        }
    }
};
</script>


<style scoped>
.code-editor {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.full-height .CodeMirror {
  width: 100%;
  height: 100%;
  flex: 1;
}
</style>