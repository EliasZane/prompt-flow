export interface Template {
  id: number
  templateCode: string
  templateName: string
  templateType: string
  description: string
  icon?: string
  status: number
  sortNum: number
}

// 兼容旧代码的别名或映射
export interface TemplateSummary extends Template {}

export type ComponentType = 'input' | 'textarea' | 'select' | 'radio' | 'tags' | 'tag' | 'slider'

export interface OptionItem {
  label: string
  value: string
}

export interface FormField {
  fieldKey: string
  label: string
  componentType: ComponentType
  required: boolean
  placeholder?: string
  defaultValue?: any
  options?: OptionItem[]
  icon?: string
  min?: number
  max?: number
}

export interface TemplateDetail extends Template {
  sceneDescription: string
  outputDescription: string
  formSchema: FormField[] // 后端 VO 已经将其反序列化为对象数组
  formFields?: FormField[] // 兼容字段，可以直接使用 formSchema
}
