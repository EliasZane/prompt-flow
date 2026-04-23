import request from '../utils/request'
import type { Template, TemplateDetail } from '../types/template'

/**
 * 查询所有启用的模板列表
 */
export const getTemplates = (): Promise<Template[]> => {
  return request.get('/templates')
}

/**
 * 根据模板编码获取模板详情
 */
export const getTemplateDetail = (templateCode: string): Promise<TemplateDetail> => {
  return request.get(`/templates/${templateCode}`)
}
