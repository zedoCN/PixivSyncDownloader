import axios from 'axios';


export type  IllustInfo = {
    id: number;
    author: number;
    caption: string;
    title: string;
    sync_time: number;
    tags: string[];
    translated_tags: string[];
    page: number;
}
export type SearchIllust = {
    illusts: IllustInfo[];
    count: number;
}

export type DownloadIllust = {
    all_total: number,
    downloaded_total: number,
    find: boolean,
    info: {
        id: number,
        page: number,
        name: string,
        hash: string,
        downloaded: boolean,
    },
}

export type Result = {
    result: boolean,
    message: string,
}

export type AccountInfo = {
    id: number,
    name: string,
    account: string,
    mail_address: string,
    sync_following: boolean,
    sync_favorite: boolean,
    participate_in_downloads: boolean,
}

export type DatabaseInfo = {
    available: string[]
    all: string[]
}
export const SearchTypeEnums: Map<string, string> = new Map();

SearchTypeEnums.set("id", "id搜索");
SearchTypeEnums.set("tag", "标签");
SearchTypeEnums.set("tags", "包含标签");
SearchTypeEnums.set("title", "标题");
SearchTypeEnums.set("title_regex", "包含标题");
SearchTypeEnums.set("author", "作者");
SearchTypeEnums.set("caption", "描述");
SearchTypeEnums.set("caption_regex", "描述包含");

export function searchIllusts(search: string, type: string, offset: number): Promise<SearchIllust> {
    return axios.get('/api/illusts', {
        params: {
            search: search,
            type: type,
            offset: offset,
            num: 8
        }
    }).then(response => response.data)
}

export function getDownloadInfo(id: number, page: number): Promise<DownloadIllust> {
    return axios.get('/api/download', {
        params: {
            id: id,
            page: page
        }
    }).then(response => response.data)
}


export function getAccounts(): Promise<AccountInfo[]> {
    return axios.get('/api/account', {
        params: {}
    }).then(response => response.data.accounts)
}

export function addAccountWithToken(token: string): Promise<Result> {
    return axios.post('/api/account', {}, {
        params: {
            operation: 'add',
            token: token
        }
    }).then(response => response.data)
}

export function addAccountWithCode(code: string): Promise<Result> {
    return axios.post('/api/account', {}, {
        params: {
            operation: 'add',
            code: code
        }
    }).then(response => response.data)
}

export function deleteAccount(id: number) {
    return axios.post('/api/account', {}, {
        params: {
            operation: 'remove',
            id: id
        }
    }).then(response => response.data)
}

export function getWebLoginUrl(): Promise<Result> {
    return axios.post('/api/account', {}, {
        params: {
            operation: 'web_login'
        }
    }).then(response => response.data)
}


export function setAccountAttribute(type: string, id: number, val: boolean): Promise<Result> {
    return axios.post('/api/account', {}, {
        params: {
            operation: 'set',
            id: id,
            type: type,
            value: val
        }
    }).then(response => response.data)

}


export type ConfigInfo = {
    database_connection_string: string,
    work_database_name: string,
    repository_path: string,
}

export function getConfigInfo(): Promise<ConfigInfo> {
    return axios.get('/api/config', {
        params: {}
    }).then(response => response.data)
}

export function getDatabaseInfo(): Promise<DatabaseInfo> {
    return axios.get('/api/database', {
        params: {}
    }).then(response => response.data)
}


export function setConfig(type: string, value: string): Promise<Result> {
    return axios.post('/api/config', {}, {
        params: {
            type: type,
            value: value
        }
    }).then(response => response.data)
}


export function setDatabase(operation: string, name: string): Promise<Result> {
    return axios.post('/api/database', {}, {
        params: {
            operation: operation,
            name: name
        }
    }).then(response => response.data)
}
