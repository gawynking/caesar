<template>
    <div class="common-editor">
        <textarea ref="textarea" v-model="value"></textarea>
    </div>
</template>

<script>
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/base16-dark.css';
import 'codemirror/theme/idea.css';
import 'codemirror/theme/eclipse.css';
import 'codemirror/addon/hint/show-hint.css';
import 'codemirror/addon/hint/show-hint.js';
import 'codemirror/addon/hint/show-hint';
import 'codemirror/addon/hint/javascript-hint';
import 'codemirror/addon/hint/sql-hint';
import 'codemirror/mode/javascript/javascript';
import 'codemirror/mode/markdown/markdown';
import 'codemirror/mode/sql/sql';
import 'codemirror/mode/php/php';
import 'codemirror/mode/python/python';
import 'codemirror/mode/shell/shell';
import 'codemirror/mode/powershell/powershell';

const CodeMirror = require('codemirror/lib/codemirror');

export default {
    name: 'CommonEditor',
    props: {
        value: {
            type: String,
            default: ''
        },
        language: {
            type: String,
            default: null
        },
        readOnly: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            commonEditor: false,
            code: '',
            coder: null,
            mode: 'x-sql',
            theme: 'default',
            modes: [
                { value: 'javascript', label: 'Javascript' },
                { value: 'x-java', label: 'Java' },
                { value: 'x-python', label: 'Python' },
                { value: 'x-sql', label: 'SQL' },
                { value: 'x-shell', label: 'Shell' },
                { value: 'x-powershell', label: 'PowerShell' },
                { value: 'x-php', label: 'PHP' }
            ],
            hintOptions: {
                completeSingle: false, // 禁用自动补全单个匹配项
                hint: (cm) => {
                    return {
                        list: [],
                        from: cm.getCursor(),
                        to: cm.getCursor()
                    };
                }
            }
        };
    },
    watch: {
        value(newVal) {
            if (this.coder && this.coder.getValue() !== newVal) {
                this.setCodeContent(newVal);
            }
        },
        language: {
            handler(language) {
                this.getCoder().then(() => {
                    // 尝试从父容器获取语法类型
                    if (language) {
                        // 获取具体的语法类型对象
                        const modeObj = this.getLanguage(language);
                        // 判断父容器传入的语法是否被支持
                        if (modeObj) {
                            this.mode = modeObj.label;
                            this.coder.setOption('mode', `text/${modeObj.value}`);
                        }
                    }
                });
            },
            immediate: true
        },
        readOnly: {
            handler(readOnly) {
                this.getCoder().then(() => {
                    this.coder.setOption('readOnly', readOnly);
                });
            },
            immediate: true
        }
    },
    computed: {
        coderOptions() {
            return {
                line: true,
                mode: 'application/json', // json数据高亮
                theme: 'eclipse', // 设置主题，记得引入对应主题才有显示
                tabSize: 4,
                lineNumbers: true, // 显示行号
                cursorHeight: 0.8, // 光标高度，默认是1
                autoCloseBrackets: true,
                matchBrackets: true, // 括号匹配
                lineWrapping: 'scroll', // 文字过长时，是换行(wrap)还是滚动(scroll)，默认是滚动
                showCursorWhenSelecting: true, // 文本选中时显示光标
                smartIndent: true, // 智能缩进
                hintOptions: this.hintOptions, // 应用 hintOptions
                extraKeys: {
                    'Ctrl-Space': 'autocomplete' // Ctrl-Space 触发自动补全
                },
                readOnly: this.readOnly
            };
        }
    },
    mounted() {
        // 初始化
        this.initialize();
    },
    methods: {
        // 初始化
        initialize() {
            // 初始化编辑器实例，传入需要被实例化的文本域对象和默认配置
            this.coder = CodeMirror.fromTextArea(
                this.$refs.textarea,
                this.coderOptions
            );
            this.coder.on('inputRead', () => {
                this.coder.showHint(this.hintOptions);
            });
            // 设置编辑器最小高度为30行，并允许自动扩展
            const minHeight = 30 * this.coder.defaultTextHeight();
            this.coder.getWrapperElement().style.minHeight = `${minHeight}px`;
            this.coder.setSize('100%', 'auto');
            // 编辑器赋值
            if (this.value || this.code) {
                this.setCodeContent(this.value || this.code);
            } else {
                this.coder.setValue('');
            }
            // 支持双向绑定
            this.coder.on('change', (coder) => {
                this.code = coder.getValue();
                if (this.$emit) {
                    this.$emit('input', this.code);
                }
            });
        },
        setCodeContent(val) {
            setTimeout(() => {
                if (!val) {
                    this.coder.setValue('');
                } else {
                    this.coder.setValue(val);
                }
            }, 300);
        },
        getCoder() {
            const that = this;
            return new Promise((resolve) => {
                (function get() {
                    if (that.coder) {
                        resolve(that.coder);
                    } else {
                        setTimeout(get, 10);
                    }
                })();
            });
        },
        getLanguage(language) {
            // 在支持的语法类型列表中寻找传入的语法类型
            return this.modes.find((mode) => {
                // 所有的值都忽略大小写，方便比较
                const currentLanguage = language.toLowerCase();
                const currentLabel = mode.label.toLowerCase();
                const currentValue = mode.value.toLowerCase();

                // 由于真实值可能不规范，例如 java 的真实值是 x-java ，所以将 value 和 label 同时和传入语法进行比较
                return (
                    currentLabel === currentLanguage || currentValue === currentLanguage
                );
            });
        },
        changeMode(val) {
            // 修改编辑器的语法配置
            this.coder.setOption('mode', `text/${val}`);
            // 获取修改后的语法
            const label = this.getLanguage(val).label.toLowerCase();
            // 允许父容器通过以下函数监听当前的语法值
            this.$emit('language-change', label);
        }
    }
};
</script>

<style lang="less">
.common-editor {
    width: 100%;
    height: 100%;

    .CodeMirror {
        direction: ltr;
        line-height: 20px;
        width: 100%;
        height: 100%;
        min-height: calc(30 * 20px); /* 设置最小高度为30行 */
    }

    .CodeMirror-hints {
        z-index: 9999 !important;
    }

    .custom-class .CodeMirror {
        width: 100%;
    }
}

.CodeMirror-hints {
    z-index: 1000;
}
</style>
