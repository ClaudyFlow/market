<template>
  <div class="user-center">
    <!-- 用户信息头部 -->
    <UserHeader :user-info="userInfo" @change-avatar="triggerAvatarUpload" @avatar-upload="handleAvatarUpload" />

    <!-- 功能菜单 -->
    <UserFunctionMenu @navigate="navigateTo" />

    <!-- 选项卡内容 -->
    <div class="content-section">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="profile">
          <el-form :model="profileForm" label-width="100px" class="profile-form">
            <el-form-item label="用户名"><el-input v-model="profileForm.username" disabled /></el-form-item>
            <el-form-item label="昵称"><el-input v-model="profileForm.nickname" placeholder="请输入昵称" /></el-form-item>
            <el-form-item label="手机号">
              <div class="phone-input">
                <el-input :model-value="profileForm.phone" disabled />
                <el-button type="primary" size="small" @click="showBindPhone = true">
                  {{ profileForm.phone ? '更换手机' : '绑定手机' }}
                </el-button>
              </div>
            </el-form-item>
            <el-form-item label="邮箱">
              <div class="email-input">
                <el-input :model-value="profileForm.email" disabled />
                <el-button type="primary" size="small" @click="showBindEmail = true">
                  {{ profileForm.email ? '更换邮箱' : '绑定邮箱' }}
                </el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="saving">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="账号安全" name="security">
          <div class="security-list">
            <div class="security-item" v-for="item in securityItems" :key="item.title">
              <div>
                <div class="item-title">{{ item.title }}</div>
                <div class="item-desc">{{ item.desc }}</div>
              </div>
              <el-button type="primary" size="small" @click="item.action">{{ item.btnText }}</el-button>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="浏览历史" name="history">
          <div class="history-list">
            <div class="history-item" v-for="item in browseHistory" :key="item.id" @click="viewProduct(item.productId)">
              <img :src="item.image" :alt="item.name" class="product-image" />
              <div class="product-info">
                <div class="product-name">{{ item.name }}</div>
                <div class="product-price">¥{{ item.price }}</div>
                <div class="browse-time">{{ item.browseTime }}</div>
              </div>
            </div>
            <el-empty v-if="browseHistory.length === 0" description="暂无浏览历史" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showChangePassword" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="旧密码"><el-input v-model="passwordForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="passwordForm.newPassword" type="password" show-password /></el-form-item>
        <el-form-item label="确认密码"><el-input v-model="passwordForm.confirmPassword" type="password" show-password /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>

    <!-- 绑定手机弹窗 -->
    <el-dialog v-model="showBindPhone" title="绑定手机" width="400px">
      <el-form :model="phoneForm" label-width="100px">
        <el-form-item label="手机号"><el-input v-model="phoneForm.phone" placeholder="请输入手机号" /></el-form-item>
        <el-form-item label="验证码">
          <div class="captcha-input">
            <el-input v-model="phoneForm.code" placeholder="请输入验证码" />
            <el-button type="primary" :disabled="captchaCountdown > 0" @click="sendPhoneCaptcha">
              {{ captchaCountdown > 0 ? `${captchaCountdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBindPhone = false">取消</el-button>
        <el-button type="primary" @click="handleBindPhone">确定</el-button>
      </template>
    </el-dialog>

    <!-- 绑定邮箱弹窗 -->
    <el-dialog v-model="showBindEmail" title="绑定邮箱" width="400px">
      <el-form :model="emailForm" label-width="100px">
        <el-form-item label="邮箱"><el-input v-model="emailForm.email" placeholder="请输入邮箱" /></el-form-item>
        <el-form-item label="验证码">
          <div class="captcha-input">
            <el-input v-model="emailForm.code" placeholder="请输入验证码" />
            <el-button type="primary" :disabled="emailCaptchaCountdown > 0" @click="sendEmailCaptcha">
              {{ emailCaptchaCountdown > 0 ? `${emailCaptchaCountdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBindEmail = false">取消</el-button>
        <el-button type="primary" @click="handleBindEmail">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@user/stores/user'
import * as userApi from '@user/api/user'
import * as authApi from '@user/api/auth'
import { useCaptchaCountdown } from '@user/composables/useCaptchaCountdown'
import { UserHeader, UserFunctionMenu } from '@user/components/user'

const router = useRouter()
const userStore = useUserStore()

// 用户信息
const userInfo = ref({ username: '', nickname: '', avatar: '', level: 1, points: 0, followingCount: 0, followerCount: 0, favoriteCount: 0 })
const activeTab = ref('profile')
const profileForm = reactive({ username: '', nickname: '', phone: '', email: '', gender: 'unknown', birthday: '', bio: '' })
const saving = ref(false)

// 密码/手机/邮箱表单
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const phoneForm = reactive({ phone: '', code: '' })
const emailForm = reactive({ email: '', code: '' })
const showChangePassword = ref(false)
const showBindPhone = ref(false)
const showBindEmail = ref(false)

// 验证码倒计时
const { countdown: captchaCountdown, sendCaptcha: sendPhoneCaptchaFn } = useCaptchaCountdown()
const { countdown: emailCaptchaCountdown, sendCaptcha: sendEmailCaptchaFn } = useCaptchaCountdown()

const sendPhoneCaptcha = async () => {
  if (!phoneForm.phone) return ElMessage.warning('请输入手机号')
  await sendPhoneCaptchaFn(() => authApi.sendCaptcha(phoneForm.phone, 'phone', 'bind'))
}

const sendEmailCaptcha = async () => {
  if (!emailForm.email) return ElMessage.warning('请输入邮箱')
  await sendEmailCaptchaFn(() => authApi.sendCaptcha(emailForm.email, 'email', 'bind'))
}

// 浏览历史
const browseHistory = ref<any[]>([])
const avatarInput = ref<HTMLInputElement | null>(null)

// 安全设置项
const securityItems = computed(() => [
  { title: '登录密码', desc: '定期修改密码可以提高账号安全性', btnText: '修改密码', action: () => showChangePassword.value = true },
  { title: '绑定手机', desc: profileForm.phone || '未绑定', btnText: profileForm.phone ? '更换手机' : '绑定手机', action: () => showBindPhone.value = true },
  { title: '绑定邮箱', desc: profileForm.email || '未绑定', btnText: profileForm.email ? '更换邮箱' : '绑定邮箱', action: () => showBindEmail.value = true }
])

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const user = await userApi.getCurrentUser()
    Object.assign(userInfo.value, {
      username: user.username, nickname: user.nickname || user.username, avatar: user.avatar,
      level: user.level || 1, points: user.points || 0, followingCount: user.followingCount || 0,
      followerCount: user.followerCount || 0, favoriteCount: user.favoriteCount || 0
    })
    Object.assign(profileForm, {
      username: user.username, nickname: user.nickname || '', phone: user.phone || '',
      email: user.email || '', gender: user.gender || 'unknown', birthday: user.birthday || '', bio: user.bio || ''
    })
  } catch { ElMessage.error('获取用户信息失败') }
}

// 获取浏览历史
const fetchBrowseHistory = async () => {
  try {
    const result = await userApi.getBrowseHistory({ page: 1, size: 10 })
    browseHistory.value = result.list || []
  } catch { console.error('获取浏览历史失败') }
}

// 保存个人资料
const saveProfile = async () => {
  saving.value = true
  try {
    await userApi.updateUserInfo({ nickname: profileForm.nickname, gender: profileForm.gender, birthday: profileForm.birthday, bio: profileForm.bio })
    ElMessage.success('保存成功')
    await fetchUserInfo()
  } catch { ElMessage.error('保存失败') }
  finally { saving.value = false }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) return ElMessage.warning('请填写完整')
  if (passwordForm.newPassword !== passwordForm.confirmPassword) return ElMessage.warning('两次密码不一致')
  try {
    await authApi.changePassword(passwordForm.oldPassword, passwordForm.newPassword)
    ElMessage.success('密码修改成功')
    showChangePassword.value = false
    Object.assign(passwordForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
  } catch { ElMessage.error('密码修改失败') }
}

// 绑定手机
const handleBindPhone = async () => {
  if (!phoneForm.phone || !phoneForm.code) return ElMessage.warning('请填写完整')
  try {
    await authApi.bindPhone(phoneForm.phone, phoneForm.code)
    ElMessage.success('绑定成功')
    showBindPhone.value = false
    await fetchUserInfo()
  } catch { ElMessage.error('绑定失败') }
}

// 绑定邮箱
const handleBindEmail = async () => {
  if (!emailForm.email || !emailForm.code) return ElMessage.warning('请填写完整')
  try {
    await authApi.bindEmail(emailForm.email, emailForm.code)
    ElMessage.success('绑定成功')
    showBindEmail.value = false
    await fetchUserInfo()
  } catch { ElMessage.error('绑定失败') }
}

// 头像上传
const triggerAvatarUpload = () => avatarInput.value?.click()
const handleAvatarUpload = async (event: Event) => {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return
  try {
    const result = await userApi.updateAvatar(file)
    userInfo.value.avatar = result.avatar
    ElMessage.success('头像更新成功')
  } catch { ElMessage.error('头像更新失败') }
}

// 导航
const navigateTo = (path: string) => router.push(path)
const viewProduct = (id: string | number) => router.push(`/item/${id}`)

onMounted(() => { fetchUserInfo(); fetchBrowseHistory() })
</script>

<style scoped>
.user-center { max-width: 1000px; margin: 0 auto; padding: 20px; }
.content-section { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.profile-form { max-width: 500px; margin-top: 20px; }
.phone-input, .email-input, .captcha-input { display: flex; gap: 10px; }
.security-list { margin-top: 20px; }
.security-item { display: flex; justify-content: space-between; align-items: center; padding: 20px 0; border-bottom: 1px solid #f0f0f0; }
.security-item:last-child { border-bottom: none; }
.item-title { font-size: 16px; font-weight: 600; color: #1a1a1a; }
.item-desc { font-size: 13px; color: #666; margin-top: 5px; }
.history-list { display: flex; flex-direction: column; gap: 15px; }
.history-item { display: flex; align-items: center; gap: 15px; padding: 15px; background: #f8f9fa; border-radius: 8px; cursor: pointer; transition: all 0.3s; }
.history-item:hover { background: #f0f0f0; }
.product-image { width: 80px; height: 80px; border-radius: 8px; object-fit: cover; }
.product-info { flex: 1; }
.product-name { font-size: 14px; font-weight: 600; color: #1a1a1a; margin-bottom: 5px; }
.product-price { font-size: 16px; font-weight: 600; color: #ff4444; }
.browse-time { font-size: 12px; color: #999; margin-top: 5px; }
</style>
                <el-radio label="unknown">保密</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="生日">
              <el-date-picker
                v-model="profileForm.birthday"
                type="date"
                placeholder="选择生日"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input
                v-model="profileForm.bio"
                type="textarea"
                :rows="4"
                placeholder="介绍一下自己吧"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="账号安全" name="security">
          <div class="security-list">
            <div class="security-item">
              <div class="item-info">
                <div class="item-title">登录密码</div>
                <div class="item-desc">定期修改密码有助于保护账号安全</div>
              </div>
              <el-button @click="showChangePassword = true">修改</el-button>
            </div>
            <div class="security-item">
              <div class="item-info">
                <div class="item-title">手机号</div>
                <div class="item-desc">{{ profileForm.phone || '未绑定' }}</div>
              </div>
              <el-button @click="showBindPhone = true">
                {{ profileForm.phone ? '更换' : '绑定' }}
              </el-button>
            </div>
            <div class="security-item">
              <div class="item-info">
                <div class="item-title">邮箱</div>
                <div class="item-desc">{{ profileForm.email || '未绑定' }}</div>
              </div>
              <el-button @click="showBindEmail = true">
                {{ profileForm.email ? '更换' : '绑定' }}
              </el-button>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="浏览历史" name="history">
          <div class="history-list">
            <div v-for="item in browseHistory" :key="item.id" class="history-item" @click="goToProduct(item.productId)">
              <el-image :src="item.image" fit="cover" class="product-image" />
              <div class="product-info">
                <div class="product-name">{{ item.name }}</div>
                <div class="product-price">¥{{ item.price }}</div>
                <div class="browse-time">{{ formatTime(item.browseTime) }}</div>
              </div>
              <el-button size="small" type="danger" @click.stop="deleteHistory(item.id)">删除</el-button>
            </div>
            <div v-if="browseHistory.length === 0" class="empty-state">
              <el-empty description="暂无浏览记录" />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showChangePassword" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>

    <!-- 绑定手机弹窗 -->
    <el-dialog v-model="showBindPhone" title="绑定手机号" width="400px">
      <el-form :model="phoneForm" label-width="80px">
        <el-form-item label="手机号">
          <el-input v-model="phoneForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="验证码">
          <div class="captcha-input">
            <el-input v-model="phoneForm.code" placeholder="请输入验证码" />
            <el-button :disabled="captchaCountdown > 0" @click="sendPhoneCaptcha">
              {{ captchaCountdown > 0 ? `${captchaCountdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBindPhone = false">取消</el-button>
        <el-button type="primary" @click="handleBindPhone">确定</el-button>
      </template>
    </el-dialog>

    <!-- 绑定邮箱弹窗 -->
    <el-dialog v-model="showBindEmail" title="绑定邮箱" width="400px">
      <el-form :model="emailForm" label-width="80px">
        <el-form-item label="邮箱">
          <el-input v-model="emailForm.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="验证码">
          <div class="captcha-input">
            <el-input v-model="emailForm.code" placeholder="请输入验证码" />
            <el-button :disabled="captchaCountdown > 0" @click="sendEmailCaptcha">
              {{ captchaCountdown > 0 ? `${captchaCountdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBindEmail = false">取消</el-button>
        <el-button type="primary" @click="handleBindEmail">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart, Star, Ticket, Location } from '@element-plus/icons-vue'
import { useUserStore } from '@user/stores/user'
import * as userApi from '@user/api/user'
import * as authApi from '@user/api/auth'
import { formatDate } from '@user/utils/format'

const router = useRouter()
const userStore = useUserStore()

const userInfo = ref({
  username: '',
  nickname: '',
  avatar: '',
  level: 1,
  points: 0,
  followingCount: 0,
  followerCount: 0,
  favoriteCount: 0
})

const activeTab = ref('profile')

const profileForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  gender: 'unknown',
  birthday: '',
  bio: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const phoneForm = reactive({
  phone: '',
  code: ''
})

const emailForm = reactive({
  email: '',
  code: ''
})

const showChangePassword = ref(false)
const showBindPhone = ref(false)
const showBindEmail = ref(false)
const captchaCountdown = ref(0)

const browseHistory = ref([])
const avatarInput = ref(null)

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const user = await userApi.getCurrentUser()
    userInfo.value = {
      username: user.username,
      nickname: user.nickname || user.username,
      avatar: user.avatar,
      level: user.level || 1,
      points: user.points || 0,
      followingCount: user.followingCount || 0,
      followerCount: user.followerCount || 0,
      favoriteCount: user.favoriteCount || 0
    }
    profileForm.username = user.username
    profileForm.nickname = user.nickname || ''
    profileForm.phone = user.phone || ''
    profileForm.email = user.email || ''
    profileForm.gender = user.gender || 'unknown'
    profileForm.birthday = user.birthday || ''
    profileForm.bio = user.bio || ''
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

// 获取浏览历史
const fetchBrowseHistory = async () => {
  try {
    const result = await userApi.getBrowseHistory({ page: 1, size: 10 })
    browseHistory.value = result.records || []
  } catch (error) {
    console.error('获取浏览历史失败', error)
  }
}

// 保存个人资料
const saveProfile = async () => {
  try {
    await userApi.updateUserInfo({
      nickname: profileForm.nickname,
      gender: profileForm.gender,
      birthday: profileForm.birthday,
      bio: profileForm.bio
    })
    ElMessage.success('保存成功')
    fetchUserInfo()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  try {
    await authApi.changePassword(passwordForm.oldPassword, passwordForm.newPassword)
    ElMessage.success('密码修改成功')
    showChangePassword.value = false
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    ElMessage.error('密码修改失败')
  }
}

// 发送手机验证码
const sendPhoneCaptcha = async () => {
  if (!phoneForm.phone) {
    ElMessage.warning('请输入手机号')
    return
  }
  try {
    await authApi.sendCaptcha(phoneForm.phone, 'phone', 'bind')
    ElMessage.success('验证码已发送')
    startCountdown()
  } catch (error) {
    ElMessage.error('发送验证码失败')
  }
}

// 发送邮箱验证码
const sendEmailCaptcha = async () => {
  if (!emailForm.email) {
    ElMessage.warning('请输入邮箱')
    return
  }
  try {
    await authApi.sendCaptcha(emailForm.email, 'email', 'bind')
    ElMessage.success('验证码已发送')
    startCountdown()
  } catch (error) {
    ElMessage.error('发送验证码失败')
  }
}

// 绑定手机
const handleBindPhone = async () => {
  if (!phoneForm.code) {
    ElMessage.warning('请输入验证码')
    return
  }
  try {
    await authApi.bindPhone(phoneForm.phone, phoneForm.code)
    ElMessage.success('绑定成功')
    showBindPhone.value = false
    profileForm.phone = phoneForm.phone
    phoneForm.phone = ''
    phoneForm.code = ''
  } catch (error) {
    ElMessage.error('绑定失败')
  }
}

// 绑定邮箱
const handleBindEmail = async () => {
  if (!emailForm.code) {
    ElMessage.warning('请输入验证码')
    return
  }
  try {
    await authApi.bindEmail(emailForm.email, emailForm.code)
    ElMessage.success('绑定成功')
    showBindEmail.value = false
    profileForm.email = emailForm.email
    emailForm.email = ''
    emailForm.code = ''
  } catch (error) {
    ElMessage.error('绑定失败')
  }
}

// 倒计时
const startCountdown = () => {
  captchaCountdown.value = 60
  const timer = setInterval(() => {
    captchaCountdown.value--
    if (captchaCountdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

// 触发头像上传
const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

// 处理头像上传
const handleAvatarUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  try {
    const result = await userApi.updateAvatar(file)
    userInfo.value.avatar = result.avatar
    ElMessage.success('头像更新成功')
  } catch (error) {
    ElMessage.error('头像更新失败')
  }
}

// 删除浏览历史
const deleteHistory = async (id) => {
  try {
    await userApi.deleteBrowseHistory(id)
    fetchBrowseHistory()
    ElMessage.success('删除成功')
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 跳转商品详情
const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

// 导航
const navigateTo = (path) => {
  router.push(path)
}

// 获取等级类型
const getLevelType = (level) => {
  const types = ['', 'info', 'success', 'warning', 'danger']
  return types[level] || 'info'
}

// 获取等级名称
const getLevelName = (level) => {
  const names = ['', '普通会员', '白银会员', '黄金会员', '钻石会员', '至尊会员']
  return names[level] || '普通会员'
}

// 格式化时间
const formatTime = (time) => {
  return formatDate(time, 'YYYY-MM-DD HH:mm')
}

onMounted(() => {
  fetchUserInfo()
  fetchBrowseHistory()
})
</script>

<style scoped>
.user-center {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.user-header {
  display: flex;
  gap: 30px;
  padding: 30px;
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  margin-bottom: 20px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.edit-avatar-btn {
  font-size: 12px;
}

.user-info-section {
  flex: 1;
}

.username {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
  margin: 0 0 10px 0;
}

.user-level {
  margin-bottom: 15px;
}

.user-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-item .label {
  font-size: 12px;
  color: var(--mall-text-muted);
}

.stat-item .value {
  font-size: 18px;
  font-weight: bold;
  color: var(--mall-primary);
}

.function-menu {
  margin-bottom: 20px;
}

.menu-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.menu-card:hover {
  border-color: var(--mall-primary);
  transform: translateY(-5px);
}

.menu-icon {
  font-size: 32px;
  color: var(--mall-primary);
  margin-bottom: 10px;
}

.menu-title {
  font-size: 14px;
  color: var(--mall-text-secondary);
}

.content-section {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 20px;
}

.profile-form {
  max-width: 500px;
  margin-top: 20px;
}

.phone-input,
.email-input {
  display: flex;
  gap: 10px;
}

.security-list {
  margin-top: 20px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid var(--mall-border);
}

.security-item:last-child {
  border-bottom: none;
}

.item-title {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
}

.item-desc {
  font-size: 13px;
  color: var(--mall-text-muted);
  margin-top: 5px;
}

.captcha-input {
  display: flex;
  gap: 10px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.history-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 14px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 5px;
}

.product-price {
  font-size: 16px;
  font-weight: bold;
  color: var(--mall-primary);
  margin-bottom: 5px;
}

.browse-time {
  font-size: 12px;
  color: var(--mall-text-muted);
}

.empty-state {
  padding: 40px;
  text-align: center;
}
</style>
