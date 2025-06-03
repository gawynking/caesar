<template>
    <div ref="container" class="monaco-editor-container"></div>
</template>

<script>
export default {
    name: 'MonacoEditor',
    props: {
        value: {
            type: String,
            default: ''
        },
        language: {
            type: String,
            default: 'sql'
        },
        theme: {
            type: String,
            default: 'vs' // vs, vs-dark, hc-black
        },
        readOnly: {
            type: Boolean,
            default: false
        },
        options: {
            type: Object,
            default: () => ({})
        }
    },
    data() {
        return {
            editor: null,
            monaco: null,
            preventTriggerChangeEvent: false
        };
    },
    watch: {
        value(newValue) {
            if (this.editor && !this.preventTriggerChangeEvent) {
                const editorValue = this.editor.getValue();
                if (editorValue !== newValue) {
                    this.editor.setValue(newValue);
                }
            }
        },
        language(newVal) {
            if (this.editor && this.monaco) {
                const model = this.editor.getModel();
                this.monaco.editor.setModelLanguage(model, newVal);
            }
        },
        theme(newVal) {
            if (this.editor && this.monaco) {
                this.monaco.editor.setTheme(newVal);
            }
        },
        readOnly(newVal) {
            if (this.editor) {
                this.editor.updateOptions({ readOnly: newVal });
            }
        },
        options: {
            deep: true,
            handler(newOptions) {
                if (this.editor) {
                    this.editor.updateOptions(newOptions);
                }
            }
        }
    },
    mounted() {
        this.initEditor();
    },
    beforeDestroy() {
        this.destroyEditor();
    },
    methods: {
        async initEditor() {
            // 动态导入 Monaco Editor
            this.monaco = await import('monaco-editor');

            const finalOptions = {
                value: this.value,
                language: this.language,
                theme: this.theme,
                readOnly: this.readOnly,
                automaticLayout: true,
                minimap: {
                    enabled: false
                },
                scrollBeyondLastLine: false,
                fontSize: 14,
                lineNumbersMinChars: 3,
                ...this.options
            };

            this.editor = this.monaco.editor.create(this.$refs.container, finalOptions);

            // 监听内容变化
            this.editor.onDidChangeModelContent(() => {
                const value = this.editor.getValue();
                if (this.value !== value) {
                    this.preventTriggerChangeEvent = true;
                    this.$emit('input', value);
                    this.$nextTick(() => {
                        this.preventTriggerChangeEvent = false;
                    });
                }
            });

            // 设置 SQL 智能提示
            this.setupSQLIntellisense();
        },
        destroyEditor() {
            if (this.editor) {
                this.editor.dispose();
                const model = this.editor.getModel();
                if (model) {
                    model.dispose();
                }
            }
        },
        setupSQLIntellisense() {
            this.monaco.languages.registerCompletionItemProvider('sql', {
                provideCompletionItems: (model, position) => {
                    const suggestions = [
                        {
                            label: 'SELECT',
                            kind: this.monaco.languages.CompletionItemKind.Keyword,
                            documentation: 'SELECT statement',
                            insertText: 'SELECT * FROM ${1:table} WHERE ${2:condition}'
                        },
                        {
                            label: 'INSERT',
                            kind: this.monaco.languages.CompletionItemKind.Keyword,
                            documentation: 'INSERT statement',
                            insertText: 'INSERT INTO ${1:table} (${2:columns}) VALUES (${3:values})'
                        },
                        // 添加更多SQL关键词...
                    ];

                    return { suggestions };
                }
            });
        },
        getEditor() {
            return this.editor;
        },
        setValue(value) {
            if (this.editor) {
                this.editor.setValue(value);
            }
        },
        getValue() {
            return this.editor ? this.editor.getValue() : '';
        },
        setLanguage(language) {
            if (this.editor && this.monaco) {
                const model = this.editor.getModel();
                this.monaco.editor.setModelLanguage(model, language);
            }
        }
    }
};
</script>

<style scoped>
.monaco-editor-container {
    width: 98%;
    /* height: 100%; */
    height: calc(100vh);
    min-height: 650px;
    border: 1px solid #ddd;
    border-radius: 4px;
}
</style>