import request from '@/utils/request'

// 使用相对路径，通过Vite代理转发请求
// const BASE_URL = 'http://localhost:8080'

export const chatAPI = {
  // 发送聊天消息
  async sendMessage(data, chatId) {
    try {
      const url = new URL(`/ai/chat`, window.location.origin)
      if (chatId) {
        url.searchParams.append('chatId', chatId)
      }
      
      // 获取用户ID
      const userId = localStorage.getItem('userId')
      const headers = {}
      if (userId) {
        headers['X-User-Id'] = userId
      }
      
      const response = await fetch(url, {
        method: 'POST',
        headers: headers,
        body: data instanceof FormData ? data : 
          new URLSearchParams({ prompt: data }),
        signal: AbortSignal.timeout(180000) // 3分钟超时（180秒）
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return response.body.getReader()
    } catch (error) {
      console.error('API Error:', error)
      throw error
    }
  },

  // 获取聊天历史列表
  async getChatHistory(type = 'chat') {  // 添加类型参数
    try {
      const response = await fetch(`/ai/history/${type}`)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      const chatIds = await response.json()
      
      // 为每个对话获取最后一条消息
      const historyWithMessages = await Promise.all(
        chatIds.map(async (id) => {
          try {
            // 获取该对话的消息列表
            const messagesResponse = await fetch(`/ai/history/${type}/${id}`)
            if (messagesResponse.ok) {
              const messages = await messagesResponse.json()
              // 获取最后一条用户消息作为标题
              const lastUserMessage = messages.filter(m => m.role === 'user').pop()
              const preview = lastUserMessage?.content || '新对话'
              // 截取前30个字符作为预览
              const title = preview.length > 30 ? preview.substring(0, 30) + '...' : preview
              
              return {
                id,
                title,
                preview: preview.length > 50 ? preview.substring(0, 50) + '...' : preview,
                messageCount: messages.length,
                timestamp: Date.now() // 可以从消息中获取实际时间戳
              }
            }
          } catch (error) {
            console.error(`获取对话 ${id} 的消息失败:`, error)
          }
          
          // 如果获取消息失败，返回默认标题
          return {
            id,
            title: type === 'pdf' ? `PDF对话 ${id}` : 
                   type === 'service' ? `咨询 ${id}` :
                   `对话 ${id}`,
            preview: '暂无消息',
            messageCount: 0,
            timestamp: Date.now()
          }
        })
      )
      
      return historyWithMessages
    } catch (error) {
      console.error('API Error:', error)
      return []
    }
  },

  // 生成新的对话ID
  async generateChatId(type = 'chat') {
    try {
      const response = await fetch(`/ai/generate-id/${type}`)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      return await response.text()
    } catch (error) {
      console.error('API Error:', error)
      // 如果API调用失败，返回时间戳作为备用
      return Date.now().toString()
    }
  },

  // 获取特定对话的消息历史
  async getChatMessages(chatId, type = 'chat') {  // 添加类型参数
    try {
      const response = await fetch(`/ai/history/${type}/${chatId}`)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      const messages = await response.json()
      // 添加时间戳
      return messages.map(msg => ({
        ...msg,
        timestamp: new Date() // 由于后端没有提供时间戳，这里临时使用当前时间
      }))
    } catch (error) {
      console.error('API Error:', error)
      return []
    }
  },

  // 删除特定对话的历史记录
  async deleteChatHistory(chatId, type = 'chat') {
    try {
      const response = await fetch(`/ai/history/${type}/${chatId}`, {
        method: 'DELETE'
      })
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      return true
    } catch (error) {
      console.error('API Error:', error)
      return false
    }
  },

  // 发送游戏消息
  async sendGameMessage(prompt, chatId) {
    try {
      const response = await fetch(`/ai/game?prompt=${encodeURIComponent(prompt)}&chatId=${chatId}`, {
        method: 'GET',
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return response.body.getReader()
    } catch (error) {
      console.error('API Error:', error)
      throw error
    }
  },

  // 发送 PDF 问答消息
  async sendPdfMessage(prompt, chatId) {
    try {
      // 获取用户ID
      const userId = localStorage.getItem('userId')
      const headers = {}
      if (userId) {
        headers['X-User-Id'] = userId
      }
      
      const response = await fetch(`/ai/pdf/chat?prompt=${encodeURIComponent(prompt)}&chatId=${chatId}`, {
        method: 'GET',
        headers: headers,
        // 确保使用流式响应
        signal: AbortSignal.timeout(180000) // 3分钟超时（180秒）
      })

      if (!response.ok) {
        throw new Error(`API error: ${response.status}`)
      }

      // 返回可读流
      return response.body.getReader()
    } catch (error) {
      console.error('API Error:', error)
      throw error
    }
  },

  // 自动分析病历PDF
  async autoAnalyzeMedicalRecord(chatId) {
    try {
      const response = await fetch(`/ai/pdf/auto-analyze/${chatId}`, {
        method: 'GET',
        // 自动分析可能需要更长时间
        signal: AbortSignal.timeout(180000), // 3分钟超时（180秒）
        headers: {
          'Accept': 'text/html;charset=utf-8',
          'Cache-Control': 'no-cache'
        }
      })

      if (!response.ok) {
        throw new Error(`分析服务响应错误: ${response.status} ${response.statusText}`)
      }

      // 检查响应是否有内容
      if (!response.body) {
        throw new Error('服务器未返回有效响应')
      }

      // 返回可读流
      return response.body.getReader()
    } catch (error) {
      console.error('自动分析API调用失败:', error)
      
      if (error.name === 'AbortError') {
        throw new Error('分析请求超时，请稍后重试')
      } else if (error.message.includes('fetch')) {
        throw new Error('网络连接失败，请检查网络连接')
      } else {
        throw error
      }
    }
  },

  // 保存对话ID（不发消息，仅注册ID）
  async saveChatId(type = 'chat', chatId) {
    try {
      const response = await fetch(`/ai/save-chat-id/${type}/${chatId}`)
      return response.ok
    } catch (error) {
      return false
    }
  }
}

// 用户相关API
export const userAPI = {
  // 用户登录
  async login(data) {
    try {
      const response = await fetch('/api/user/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      })
      
      const result = await response.json()
      
      if (result.code !== '200') {
        throw new Error(result.msg || '登录失败')
      }
      
      return result
    } catch (error) {
      console.error('登录请求失败:', error)
      throw error
    }
  },

  // 上传头像到MinIO
  async uploadAvatar(file) {
    try {
      const formData = new FormData()
      formData.append('file', file)
      
      const response = await fetch('/api/user/upload-avatar', {
        method: 'POST',
        body: formData,
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })
      
      if (!response.ok) {
        throw new Error(`上传失败: ${response.status}`)
      }
      
      const result = await response.json()
      
      if (result.code !== '200') {
        throw new Error(result.msg || '上传头像失败')
      }
      
      return result.data // 返回图片URL
    } catch (error) {
      console.error('上传头像失败:', error)
      throw error
    }
  },

  // 获取用户头像URL
  async getAvatarUrl() {
    try {
      const response = await fetch('/api/user/avatar', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })
      
      if (!response.ok) {
        return null
      }
      
      const result = await response.json()
      return result.code === '200' ? result.data : null
    } catch (error) {
      console.error('获取头像失败:', error)
      return null
    }
  }
}

// 医疗数据统计API
export const healthAPI = {
  // 获取用户健康统计数据
  async getHealthStats() {
    try {
      const userId = localStorage.getItem('userId')
      const headers = {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
      if (userId) headers['X-User-Id'] = userId
      const response = await fetch('/api/health/stats', {
        headers
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const result = await response.json()
      // /api/health/stats currently returns the statistics object directly.
      // Also accept the common ResponseResult envelope for forward compatibility.
      if (result && (result.code === 200 || result.code === '200')) return result.data
      return result
    } catch (error) {
      console.error('获取健康统计失败:', error)
      // 接口异常时返回空对象，由后端真实数据驱动
      return null

    }
  },

  async getTodayReminders() {
    const userId = localStorage.getItem('userId')
    if (!userId) return { todayList: [], todayReminders: 0 }

    const response = await fetch(`/api/medicine/today?userId=${encodeURIComponent(userId)}`, {
      headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
    })
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)

    const result = await response.json()
    if (result.code !== 200 && result.code !== '200') {
      throw new Error(result.msg || '获取今日待办失败')
    }
    return result.data || { todayList: [], todayReminders: 0 }
  },

  // 获取症状统计数据
  async getSymptomStats() {
    try {
      const response = await fetch('/api/health/symptoms/stats', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const result = await response.json()
      return result.code === '200' ? result.data : null
    } catch (error) {
      console.error('获取症状统计失败:', error)
      return null
    }
  },

  // 获取药品查询统计
  async getMedicineStats() {
    try {
      const response = await fetch('/api/health/medicine/stats', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const result = await response.json()
      return result.code === '200' ? result.data : null
    } catch (error) {
      console.error('获取药品统计失败:', error)
      return null
    }
  }
}

// 会员与商业化 API（对应修改建议 2.2 商业模式）
export const memberAPI = {
  // 获取当前用户会员订阅状态
  async getSubscription() {
    try {
      const userId = localStorage.getItem('userId')
      const headers = { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
      if (userId) headers['X-User-Id'] = userId
      const response = await fetch('/api/member/subscription', {
        headers
      })
      if (!response.ok) return null
      const result = await response.json()
      return result.code === '200' ? result.data : null
    } catch (e) {
      console.error('获取会员信息失败', e)
      return null
    }
  },
  // 获取套餐列表（可后端提供或前端静态）
  async getPlans() {
    try {
      const userId = localStorage.getItem('userId')
      const headers = { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
      if (userId) headers['X-User-Id'] = userId
      const response = await fetch('/api/member/plans', {
        headers
      })
      if (!response.ok) return null
      const result = await response.json()
      return result.code === '200' ? result.data : null
    } catch (e) {
      console.error('获取套餐列表失败', e)
      return null
    }
  }
}

// 保持向后兼容
export const login = userAPI.login
