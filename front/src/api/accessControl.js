import request from '@/utils/request'

export function getAccessControls(params) {
    return request({
        url: '/api/machine/getMachines',
        method: 'get',
        params: {
            page: 1,
            row: 10,
            machineTypeCd: '9998'
        }
    })
}

export function getAccessControlsByCondition(params) {
    return request({
        url: '/api/machine/getMachines',
        method: 'get',
        params
    })
}

export function deleteAccessControls(params) {
    return request({
        url: '/api/machine/deleteMachine',
        method: 'post',
        data:params
    })
}