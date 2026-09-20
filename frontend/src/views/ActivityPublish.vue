<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addActivity } from '@/api/activity'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  title: '',
  description: '',
  maxPeople: 30,
  deadline: '',
})

const rules = {
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' },
    { max: 100, message: '标题不能超过100个字', trigger: 'blur' },
  ],
  maxPeople: [{ required: true, message: '请设置最大报名人数', trigger: 'blur' }],
  deadline: [{ required: true, message: '请选择报名截止时间', trigger: 'change' }],
}

/** 禁用今天之前的日期（当天仍可选，具体时刻由后端校验必须晚于当前时间） */
function disabledDate(date) {
  return date.getTime() < new Date().setHours(0, 0, 0, 0)
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await addActivity({
      title: form.title,
      description: form.description,
      maxPeople: form.maxPeople,
      // 契约字段 deadline，使用 ISO 格式传给后端
      deadline: form.deadline,
    })
    ElMessage.success(res.msg || '发布成功')
    router.push('/activity/list')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <el-card>
    <h3 class="page-title">发布活动</h3>
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="110px"
      class="publish-form"
    >
      <el-form-item label="活动标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入活动标题（不超过100个字）" maxlength="100" show-word-limit />
      </el-form-item>
      <el-form-item label="活动描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="5"
          placeholder="请输入活动介绍、时间地点、注意事项等"
        />
      </el-form-item>
      <el-form-item label="最大报名人数" prop="maxPeople">
        <el-input-number v-model="form.maxPeople" :min="1" :max="10000" />
      </el-form-item>
      <el-form-item label="报名截止时间" prop="deadline">
        <el-date-picker
          v-model="form.deadline"
          type="datetime"
          placeholder="请选择报名截止时间"
          value-format="YYYY-MM-DDTHH:mm:ss"
          :disabled-date="disabledDate"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSubmit">发布活动</el-button>
        <el-button @click="router.push('/activity/list')">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<style scoped>
.page-title {
  margin: 0 0 20px;
}

.publish-form {
  max-width: 640px;
}
</style>
