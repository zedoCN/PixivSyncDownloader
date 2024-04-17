import './style.css'
import "bootstrap/scss/bootstrap.scss";
import {Toast} from 'bootstrap/dist/js/bootstrap.esm.js'
import '@popperjs/core/dist/umd/popper.min.js'
import {
    AccountInfo,
    addAccountWithCode,
    addAccountWithToken, ConfigInfo, DatabaseInfo,
    deleteAccount,
    getAccounts, getConfigInfo, getDatabaseInfo,
    getWebLoginUrl, Result, setAccountAttribute, setConfig, setDatabase
} from "./serverApi.ts";
import {createApp, watch} from "vue/dist/vue.esm-bundler.js";

import '@fortawesome/fontawesome-free/css/all.css';


enum MessageIcon {
    info = 'fa-solid fa-circle-exclamation',
    warning = 'fa-solid fa-triangle-exclamation text-warning',
    danger = 'fa-solid fa-circle-exclamation text-danger',
}


const app = createApp({
    methods: {
        formatBytes(bytes: number): string {
            const units = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
            let index = 0;

            while (bytes >= 1024 && index < units.length - 1) {
                bytes /= 1024;
                index++;
            }

            return `${bytes.toFixed(2)} ${units[index]}`;
        },
        formatTimestamp(milliseconds: number): string {
            const days = Math.floor(milliseconds / (1000 * 60 * 60 * 24));
            const hours = Math.floor((milliseconds % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            const minutes = Math.floor((milliseconds % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((milliseconds % (1000 * 60)) / 1000);

            const formattedTimeParts = [];

            if (days > 0) {
                formattedTimeParts.push(`${days}d`);
            }

            if (hours > 0) {
                formattedTimeParts.push(`${hours}h`);
            }

            if (minutes > 0) {
                formattedTimeParts.push(`${minutes}m`);
            }

            if (seconds > 0 || formattedTimeParts.length === 0) {
                formattedTimeParts.push(`${seconds}s`);
            }

            return formattedTimeParts.join(' ');
        },
        getCurrentTimestamp(): number {
            return new Date().getTime(); // 返回当前时间戳（以毫秒为单位）
        },
        refreshAccounts() {
            getAccounts().then((data) => {
                this.accounts = data;
            })
        },
        refreshDatabase() {
            getDatabaseInfo().then((data) => {
                this.database = data;
                this.setting.database.existence = this.database.all.includes(this.setting.database.name)
                this.setting.database.available = this.database.available.includes(this.setting.database.name)
            })
        },
        addAccountWithToken() {
            addAccountWithToken(this.login.tokenValue).then((data) => {
                this.login.tokenValueError = data.result ? '' : data.message;
                this.showResultMessage(data)
                if (data.result) {
                    this.refreshAccounts()
                    this.login.tokenValue = ''
                }
            })
        },
        addAccountWithCode() {
            addAccountWithCode(this.login.codeValue).then((data) => {
                this.login.codeValueError = data.result ? '' : data.message;
                this.showResultMessage(data)
                if (data.result) {
                    this.refreshAccounts()
                    this.login.codeValue = ''
                }
            })
        },
        deleteAccount() {
            deleteAccount(this.showAccount.id).then((data) => {
                this.showResultMessage(data)
                this.refreshAccounts()
            })
        },
        refreshWebLogin() {
            getWebLoginUrl().then((data) => {
                this.login.webLoginUrl = data.message
            })
        },
        refreshConfig() {
            getConfigInfo().then((data) => {
                this.config = data
                if (!this.database.available.includes(this.config.work_database_name)) {
                    this.config.work_database_name = ''
                }
            })
        },
        showResultMessage(message: Result) {
            this.showMessage((message.result ? MessageIcon.info : MessageIcon.warning), message.message)
        },
        showMessage(icon: MessageIcon, message: string) {
            if (message == null)
                return
            this.messageToast.icon = icon
            this.messageToast.message = message
            const iconTitles = {
                [MessageIcon.info]: '信息',
                [MessageIcon.warning]: '警告',
                [MessageIcon.danger]: '错误'
            };

            this.messageToast.title = iconTitles[icon];
            this.messageToast.toastBootstrap.show()
        },
        setConfig(type: string, value: string) {
            setConfig(type, value).then((data) => {
                this.showResultMessage(data)
                if (data.result) {
                    this.refreshDatabase()
                    this.refreshConfig()
                }
            })
        },
        createDatabase(name: string) {
            setDatabase('create', name).then((data) => {
                this.showResultMessage(data)
                if (data.result) {
                    this.refreshDatabase()
                }
            })
        },
        deleteDatabase(name: string) {
            setDatabase('delete', name).then((data) => {
                this.showResultMessage(data)
                if (data.result) {
                    this.refreshDatabase()
                }
            })
        }
    },
    setup() {


    },
    mounted() {
        //挂载 初始化

        this.messageToast.toastBootstrap = Toast.getOrCreateInstance(document.getElementById('messageToast'))


        const codeRegex = /^[a-zA-Z0-9_-]{43}$/;
        watch(
            () => this.login.tokenValue,
            (value) => {
                let isJson = value.indexOf('{') != -1;
                if (isJson) {
                    let json;
                    try {
                        json = JSON.parse(value);
                    } catch (error) {
                        return
                    }
                    this.login.tokenValue = json.refresh_token
                }

                this.login.tokenValueError = (codeRegex.test(value) || value == '' ? '' : '格式不正确！');
            }
        )
        watch(
            () => this.login.codeValue,
            (value) => {
                const uriRegex = /code=([^&]+)/;
                const match = value.match(uriRegex);
                if (match) {
                    this.login.codeValue = match[1];
                }
                this.login.codeValueError = (codeRegex.test(value) || value == '' ? '' : '格式不正确！');
            }
        )
        watch(() => this.showAccount.sync_following, (value) => {
            setAccountAttribute('sync_following', this.showAccount.id, value)
        })
        watch(() => this.showAccount.sync_favorite, (value) => {
            setAccountAttribute('sync_favorite', this.showAccount.id, value)
        })
        watch(() => this.showAccount.participate_in_downloads, (value) => {
            setAccountAttribute('participate_in_downloads', this.showAccount.id, value)
        })
        watch(() => this.setting.database.name, (value) => {
            this.setting.database.existence = this.database.all.includes(value)
            this.setting.database.available = this.database.available.includes(value)
        })
        this.refreshAccounts()
        this.refreshWebLogin()
        this.refreshDatabase()
        this.refreshConfig()
    },
    data() {

        return {
            database: {} as DatabaseInfo,
            setting: {
                database: {
                    name: '',
                    existence: false,
                    available: false
                },
                work_database_name: ''
            },
            statistics: {
                totalIllustrationCount: 100,
                followArtistCount: 150,
                cumulativeTrafficConsumption: 1024 * 1024,
                synchronousCount: 15,
                lastSynchronousTimestamp: 1712548760552,
            },
            accounts: [] as AccountInfo[],
            login: {
                tokenValue: '',
                tokenValueError: '',
                codeValue: '',
                codeValueError: '',
                webLoginUrl: ''
            },
            config: {} as ConfigInfo,
            showAccount: {} as AccountInfo,
            messageToast: {
                message: '消息本体',
                title: '标题',
                icon: 'fa-solid fa-triangle-exclamation',
                toastBootstrap: {}
            },
            MessageIcon: MessageIcon,
        }
    }
})
app.mount('#app')



