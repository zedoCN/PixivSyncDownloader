import './style.css'
import "bootstrap/scss/bootstrap.scss";
import {getDownloadInfo, searchIllusts, SearchTypeEnums} from './serverApi.ts'
import {createApp} from 'vue/dist/vue.esm-bundler.js'


const app = createApp({
    methods: {
        search() {
            searchIllusts(this.searchValue, this.searchType, (this.navigation.select - 1) * 8).then((res) => {
                this.showIllusts = res.illusts;
                this.navigation.counts = res.count
                this.navigation.items = []


                for (let i = Math.max(this.navigation.select - 3, 0); i < Math.min(Math.ceil(res.count / 8), this.navigation.select + 2); i++) {
                    this.navigation.items.push({
                        id: i + 1,
                    })
                }

                for (let i in this.showIllusts) {
                    this.getIllustInfo(this.showIllusts[i])
                }

            })
        },
        getIllustInfo(illustInfo) {
            getDownloadInfo(illustInfo.id, illustInfo.page).then(res => {
                illustInfo.downloadInfo = res.info;
                illustInfo.url = '/api/image?name=' + res.info.name + '&type=preview&quality=0.2'
                illustInfo.original = '/api/image?name=' + res.info.name
            });
        }
    },
    setup() {


    },
    mounted() {
        this.searchValue = '萝莉'
        this.searchType = 'title_regex'
        this.search()
    },
    data() {
        return {
            showIllusts: [],
            count: 0,
            searchTypes: SearchTypeEnums,
            searchType: 'id',
            searchValue: '',
            navigation: {
                counts: 0,
                items: [{
                    id: 1
                }],
                select: 1
            },
        }
    }
})
app.mount('#app')


