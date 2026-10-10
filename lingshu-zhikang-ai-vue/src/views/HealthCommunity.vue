<template>
  <div class="health-community">
    <div class="community-header">
      <h1>👥 健康社区</h1>
      <button @click="showCreatePost = true" class="create-post-btn">发帖</button>
    </div>

    <!-- Tab切换 -->
    <div class="community-tabs">
      <button 
        v-for="tab in tabs" 
        :key="tab.key"
        :class="['tab-btn', { active: activeTab === tab.key }]"
        @click="switchTab(tab.key)">
        {{ tab.label }}
      </button>
    </div>

    <!-- 分类筛选 -->
    <div v-if="activeTab === 'all' || activeTab === 'category'" class="category-filter">
      <button 
        v-for="cat in categories" 
        :key="cat"
        :class="['category-btn', { active: selectedCategory === cat }]"
        @click="filterByCategory(cat)">
        {{ cat }}
      </button>
    </div>

    <!-- 帖子列表 -->
    <div class="posts-list">
      <div v-for="post in posts" :key="post.id" class="post-card" @click="viewPost(post.id)">
        <div class="post-header">
          <div class="post-author">
            <div class="author-avatar">{{ post.isAnonymous ? '🎭' : '👤' }}</div>
            <span class="author-name">{{ post.isAnonymous ? '匿名用户' : '用户' + post.userId }}</span>
          </div>
          <div class="post-badges">
            <span v-if="post.isTop" class="badge top">置顶</span>
            <span v-if="post.isEssence" class="badge essence">精华</span>
            <span class="badge category">{{ post.category }}</span>
            <span v-if="post.diseaseTag" class="badge disease">{{ post.diseaseTag }}</span>
          </div>
        </div>

        <h2 class="post-title">{{ post.title }}</h2>
        
        <div class="post-content">
          {{ post.content.substring(0, 200) }}{{ post.content.length > 200 ? '...' : '' }}
        </div>

        <div class="post-footer">
          <div class="post-stats">
            <span class="stat"><span class="icon">👁️</span> {{ post.viewCount }}</span>
            <span class="stat"><span class="icon">👍</span> {{ post.likeCount }}</span>
            <span class="stat"><span class="icon">💬</span> {{ post.commentCount }}</span>
            <span class="stat"><span class="icon">⭐</span> {{ post.collectCount }}</span>
          </div>
          <div class="post-time">{{ formatTime(post.createdTime) }}</div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <button @click="prevPage" :disabled="page === 1">上一页</button>
      <span>第 {{ page }} 页</span>
      <button @click="nextPage">下一页</button>
    </div>

    <!-- 创建帖子对话框 -->
    <div v-if="showCreatePost" class="dialog-overlay" @click="showCreatePost = false">
      <div class="dialog-content create-post-dialog" @click.stop>
        <h3>✍️ {{ editingPostId ? '编辑帖子' : '发布帖子' }}</h3>
        <form @submit.prevent="createPost">
          <div class="form-group">
            <label>分类</label>
            <select v-model="newPost.category" required>
              <option value="经验分享">经验分享</option>
              <option value="求助问答">求助问答</option>
              <option value="病友日记">病友日记</option>
              <option value="健康资讯">健康资讯</option>
            </select>
          </div>

          <div class="form-group">
            <label>疾病标签（可选）</label>
            <select v-model="newPost.diseaseTag">
              <option value="">无</option>
              <option value="高血压">高血压</option>
              <option value="糖尿病">糖尿病</option>
              <option value="心脏病">心脏病</option>
              <option value="其他">其他</option>
            </select>
          </div>

          <div class="form-group">
            <label>标题</label>
            <input v-model="newPost.title" required placeholder="请输入标题" />
          </div>

          <div class="form-group">
            <label>内容</label>
            <textarea v-model="newPost.content" required placeholder="分享您的经验或问题..."></textarea>
          </div>

          <div class="form-group checkbox">
            <label>
              <input type="checkbox" v-model="newPost.isAnonymous" />
              匿名发帖
            </label>
          </div>

          <div class="dialog-actions">
            <button type="button" @click="showCreatePost = false">取消</button>
            <button type="submit">发布</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 帖子详情对话框 -->
    <div v-if="showPostDetail" class="dialog-overlay" @click="showPostDetail = false">
      <div class="dialog-content post-detail-dialog" @click.stop>
        <div v-if="currentPost" class="post-detail">
          <div class="detail-header">
            <h2>{{ currentPost.title }}</h2>
            <button @click="showPostDetail = false" class="close-btn">✕</button>
          </div>

          <div class="detail-meta">
            <span class="author">
              {{ currentPost.isAnonymous ? '匿名用户' : '用户' + currentPost.userId }}
            </span>
            <span class="time">{{ formatTime(currentPost.createdTime) }}</span>
          </div>

          <div class="detail-badges">
            <span class="badge category">{{ currentPost.category }}</span>
            <span v-if="currentPost.diseaseTag" class="badge disease">{{ currentPost.diseaseTag }}</span>
          </div>

          <div class="detail-content">
            {{ currentPost.content }}
          </div>

          <div class="detail-actions">
            <button @click="toggleLike(currentPost.id)" :class="['action-btn', { active: currentPost.liked }]">
              👍 点赞 ({{ currentPost.likeCount }})
            </button>
            <button @click="toggleCollect(currentPost.id)" :class="['action-btn', { active: currentPost.collected }]">
              ⭐ 收藏 ({{ currentPost.collectCount }})
            </button>
            <button class="action-btn">
              💬 评论 ({{ currentPost.commentCount }})
            </button>
            <button v-if="currentPost.userId === userId" class="action-btn" @click="startEditPost">✏️ 编辑</button>
            <button v-if="currentPost.userId === userId" class="action-btn danger" @click="deletePost(currentPost.id)">🗑️ 删除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'HealthCommunity',
  data() {
    return {
      activeTab: 'all',
      tabs: [
        { key: 'all', label: '全部' },
        { key: 'hot', label: '热门' },
        { key: 'essence', label: '精华' },
        { key: 'following', label: '关注' }
      ],
      selectedCategory: '全部',
      categories: ['全部', '经验分享', '求助问答', '病友日记', '健康资讯'],
      posts: [],
      page: 1,
      pageSize: 10,
      showCreatePost: false,
      showPostDetail: false,
      currentPost: null,
      editingPostId: null,
      newPost: {
        category: '',
        diseaseTag: '',
        title: '',
        content: '',
        isAnonymous: false
      },
      userId: 1
    }
  },
  mounted() {
    this.userId = Number(localStorage.getItem('userId') || 1)
    this.loadPosts()
  },
  methods: {
    async loadPosts() {
      try {
        let url = 'http://localhost:8080/api/community/post/list'
        let params = {
          page: this.page,
          pageSize: this.pageSize
        }

        if (this.activeTab === 'hot') {
          url = 'http://localhost:8080/api/community/post/hot'
          params = { limit: 20 }
        } else if (this.activeTab === 'essence') {
          url = 'http://localhost:8080/api/community/post/essence'
        } else if (this.selectedCategory !== '全部') {
          url = `http://localhost:8080/api/community/post/category/${this.selectedCategory}`
        }

        const response = await axios.get(url, { params })
        if (response.data.code === 200) {
          this.posts = response.data.data || []
        }
      } catch (error) {
        console.error('加载帖子列表失败:', error)
      }
    },

    switchTab(tabKey) {
      this.activeTab = tabKey
      this.page = 1
      this.loadPosts()
    },

    filterByCategory(category) {
      this.selectedCategory = category
      this.page = 1
      this.loadPosts()
    },

    async viewPost(postId) {
      try {
        const response = await axios.get(`http://localhost:8080/api/community/post/${postId}`)
        if (response.data.code === 200) {
          this.currentPost = response.data.data
          this.showPostDetail = true
        }
      } catch (error) {
        console.error('加载帖子详情失败:', error)
      }
    },

    async createPost() {
      try {
        const postData = {
          ...this.newPost,
          userId: this.userId
        }

        const response = await axios[this.editingPostId ? 'put' : 'post']('http://localhost:8080/api/community/post', {
          ...postData,
          id: this.editingPostId
        })
        if (response.data.code === 200) {
          alert(this.editingPostId ? '修改成功！' : '发帖成功！')
          this.showCreatePost = false
          this.editingPostId = null
          this.newPost = {
            category: '',
            diseaseTag: '',
            title: '',
            content: '',
            isAnonymous: false
          }
          this.loadPosts()
        }
      } catch (error) {
        console.error('发帖失败:', error)
        alert('发帖失败，请稍后重试')
      }
    },

    startEditPost() {
      this.editingPostId = this.currentPost.id
      this.newPost = {
        category: this.currentPost.category,
        diseaseTag: this.currentPost.diseaseTag || '',
        title: this.currentPost.title,
        content: this.currentPost.content,
        isAnonymous: this.currentPost.isAnonymous
      }
      this.showPostDetail = false
      this.showCreatePost = true
    },

    async deletePost(postId) {
      if (!window.confirm('确定删除这篇帖子吗？')) return
      try {
        const response = await axios.delete(`http://localhost:8080/api/community/post/${postId}`, { params: { userId: this.userId } })
        if (response.data.code === 200) {
          this.showPostDetail = false
          this.currentPost = null
          await this.loadPosts()
        }
      } catch (error) {
        console.error('删除帖子失败:', error)
        alert('删除失败，请稍后重试')
      }
    },

    async toggleLike(postId) {
      try {
        const response = await axios.post(
          `http://localhost:8080/api/community/post/${postId}/like`,
          null,
          { params: { userId: this.userId } }
        )
        if (response.data.code === 200) {
          if (this.currentPost) {
            this.currentPost.likeCount++
            this.currentPost.liked = true
          }
          this.loadPosts()
        }
      } catch (error) {
        console.error('点赞失败:', error)
      }
    },

    async toggleCollect(postId) {
      try {
        const response = await axios.post(
          `http://localhost:8080/api/community/post/${postId}/collect`,
          null,
          { params: { userId: this.userId } }
        )
        if (response.data.code === 200) {
          if (this.currentPost) {
            this.currentPost.collectCount++
            this.currentPost.collected = true
          }
          alert('收藏成功！')
        }
      } catch (error) {
        console.error('收藏失败:', error)
      }
    },

    prevPage() {
      if (this.page > 1) {
        this.page--
        this.loadPosts()
      }
    },

    nextPage() {
      this.page++
      this.loadPosts()
    },

    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now - date

      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
      if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
      if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'

      return date.toLocaleDateString('zh-CN')
    }
  }
}
</script>

<style>
.health-community {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  min-height: 100vh;
  width: 100vw;
  background: #2e7d32; /* 深绿色背景 */
  padding: 20px;
  margin: 0;
  box-sizing: border-box;
  overflow-y: auto;
}

.health-community > * {
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.community-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.community-header h1 {
  font-size: 32px;
  color: #ffffff;
}

.create-post-btn {
  padding: 12px 24px;
  background: #27ae60;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
}

.community-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  border-bottom: 2px solid #ecf0f1;
}

.tab-btn {
  padding: 12px 24px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 16px;
  color: #7f8c8d;
  transition: all 0.3s;
  border-bottom: 3px solid transparent;
}

.tab-btn.active {
  color: #3498db;
  border-bottom-color: #3498db;
}

.category-filter {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.category-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.category-btn.active {
  background: #3498db;
  color: white;
  border-color: #3498db;
}

.posts-list {
  display: grid;
  gap: 20px;
  margin-bottom: 30px;
}

.post-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.post-card:hover {
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  transform: translateY(-2px);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #ecf0f1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.author-name {
  font-weight: 500;
  color: #2c3e50;
}

.post-badges {
  display: flex;
  gap: 8px;
}

.badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  color: white;
}

.badge.top {
  background: #e74c3c;
}

.badge.essence {
  background: #f39c12;
}

.badge.category {
  background: #3498db;
}

.badge.disease {
  background: #9b59b6;
}

.post-title {
  font-size: 20px;
  color: #2c3e50;
  margin-bottom: 10px;
}

.post-content {
  color: #555;
  line-height: 1.6;
  margin-bottom: 15px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #ecf0f1;
}

.post-stats {
  display: flex;
  gap: 20px;
}

.stat {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #7f8c8d;
  font-size: 14px;
}

.stat .icon {
  font-size: 16px;
}

.post-time {
  color: #95a5a6;
  font-size: 14px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
}

.pagination button {
  padding: 10px 20px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog-content {
  background: white;
  border-radius: 12px;
  max-width: 90vw;
  max-height: 90vh;
  overflow-y: auto;
}

.create-post-dialog {
  padding: 30px;
  width: 600px;
}

.post-detail-dialog {
  padding: 40px;
  width: 800px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #2c3e50;
  font-weight: 500;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.form-group textarea {
  min-height: 200px;
  resize: vertical;
}

.form-group.checkbox label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.dialog-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 20px;
}

.dialog-actions button {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.dialog-actions button[type="button"] {
  background: #ecf0f1;
  color: #2c3e50;
}

.dialog-actions button[type="submit"] {
  background: #27ae60;
  color: white;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.detail-header h2 {
  flex: 1;
  margin-right: 20px;
  color: #2c3e50;
}

.close-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: #ecf0f1;
  border-radius: 50%;
  cursor: pointer;
  font-size: 20px;
}

.detail-meta {
  display: flex;
  gap: 20px;
  color: #7f8c8d;
  margin-bottom: 15px;
}

.detail-badges {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.detail-content {
  padding: 30px 0;
  color: #2c3e50;
  line-height: 1.8;
  font-size: 16px;
  border-top: 1px solid #ecf0f1;
  border-bottom: 1px solid #ecf0f1;
}

.detail-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

.action-btn {
  padding: 10px 20px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-btn:hover {
  background: #f8f9fa;
}

.action-btn.active {
  background: #3498db;
  color: white;
  border-color: #3498db;
}
</style>

