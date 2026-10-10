<template>
  <Teleport to="body">
    <div class="logout-dialog-backdrop" @mousedown.self="emit('cancel')">
      <section
        class="logout-dialog"
        role="alertdialog"
        aria-modal="true"
        aria-labelledby="logout-dialog-title"
        aria-describedby="logout-dialog-description"
      >
        <button class="close-button" type="button" aria-label="关闭退出确认" @click="emit('cancel')">
          <XMarkIcon />
        </button>

        <div class="dialog-icon" aria-hidden="true">
          <ArrowRightOnRectangleIcon />
        </div>

        <div class="dialog-copy">
          <span class="eyebrow">账户安全</span>
          <h2 id="logout-dialog-title">要退出当前账号吗？</h2>
          <p id="logout-dialog-description">
            退出后将返回登录页面，本设备上的健康数据不会被删除。
          </p>
        </div>

        <div class="security-note">
          <ShieldCheckIcon aria-hidden="true" />
          <span>在公共设备上使用时，建议退出以保护个人健康信息。</span>
        </div>

        <div class="dialog-actions">
          <button ref="cancelButton" class="secondary-button" type="button" @click="emit('cancel')">
            继续使用
          </button>
          <button class="primary-button" type="button" @click="emit('confirm')">
            <ArrowRightOnRectangleIcon />
            确认退出
          </button>
        </div>
      </section>
    </div>
  </Teleport>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import {
  ArrowRightOnRectangleIcon,
  ShieldCheckIcon,
  XMarkIcon
} from '@heroicons/vue/24/outline'

const emit = defineEmits(['confirm', 'cancel'])
const cancelButton = ref(null)

const handleKeydown = (event) => {
  if (event.key === 'Escape') emit('cancel')
}

onMounted(async () => {
  document.addEventListener('keydown', handleKeydown)
  await nextTick()
  cancelButton.value?.focus()
})

onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped lang="scss">
.logout-dialog-backdrop {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: grid;
  place-items: center;
  padding: 24px;
  background: rgba(19, 35, 52, 0.48);
  backdrop-filter: blur(8px);
  animation: backdrop-in 180ms ease-out;
}

.logout-dialog {
  position: relative;
  width: min(440px, 100%);
  overflow: hidden;
  border: 1px solid rgba(206, 222, 233, 0.9);
  border-radius: 24px;
  padding: 36px;
  background:
    radial-gradient(circle at 100% 0, rgba(40, 181, 165, 0.12), transparent 34%),
    #fff;
  box-shadow: 0 28px 80px rgba(18, 47, 66, 0.24);
  animation: dialog-in 220ms cubic-bezier(0.2, 0.8, 0.2, 1);
}

.close-button {
  position: absolute;
  top: 18px;
  right: 18px;
  width: 36px;
  height: 36px;
  display: grid;
  place-items: center;
  border: 0;
  border-radius: 10px;
  color: #718295;
  background: transparent;
  cursor: pointer;
  transition: 160ms ease;

  svg { width: 20px; }
  &:hover { color: #17344a; background: #eef5f7; }
}

.dialog-icon {
  width: 58px;
  height: 58px;
  display: grid;
  place-items: center;
  margin-bottom: 24px;
  border: 1px solid #bde8e1;
  border-radius: 18px;
  color: #168f83;
  background: linear-gradient(145deg, #effbf8, #e2f6f2);
  box-shadow: 0 10px 24px rgba(22, 143, 131, 0.13);

  svg { width: 28px; }
}

.dialog-copy {
  .eyebrow {
    display: block;
    margin-bottom: 7px;
    color: #168f83;
    font-size: 12px;
    font-weight: 800;
    letter-spacing: 0.12em;
  }

  h2 {
    margin: 0;
    color: #172f43;
    font-size: 25px;
    line-height: 1.3;
    letter-spacing: -0.02em;
  }

  p {
    margin: 12px 0 0;
    color: #697b8c;
    font-size: 14px;
    line-height: 1.75;
  }
}

.security-note {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-top: 24px;
  border-radius: 14px;
  padding: 13px 14px;
  color: #5e7182;
  background: #f4f8fa;
  font-size: 12px;
  line-height: 1.6;

  svg {
    width: 19px;
    flex: 0 0 auto;
    color: #25998d;
  }
}

.dialog-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 28px;

  button {
    min-height: 48px;
    border-radius: 13px;
    font: inherit;
    font-size: 14px;
    font-weight: 800;
    cursor: pointer;
    transition: transform 160ms ease, box-shadow 160ms ease, border-color 160ms ease;

    &:hover { transform: translateY(-1px); }
    &:focus-visible { outline: 3px solid rgba(38, 166, 154, 0.24); outline-offset: 2px; }
  }
}

.secondary-button {
  border: 1px solid #d6e1e8;
  color: #455b6d;
  background: #fff;

  &:hover { border-color: #9eb9c5; background: #f9fbfc; }
}

.primary-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 1px solid transparent;
  color: #fff;
  background: linear-gradient(135deg, #168f83, #20ad9d);
  box-shadow: 0 10px 22px rgba(22, 143, 131, 0.22);

  svg { width: 18px; }
  &:hover { box-shadow: 0 13px 28px rgba(22, 143, 131, 0.3); }
}

@keyframes backdrop-in {
  from { opacity: 0; }
}

@keyframes dialog-in {
  from { opacity: 0; transform: translateY(12px) scale(0.97); }
}

@media (max-width: 520px) {
  .logout-dialog-backdrop { padding: 16px; align-items: end; }
  .logout-dialog { padding: 30px 22px 22px; border-radius: 22px; }
  .dialog-actions { grid-template-columns: 1fr; }
  .secondary-button { order: 2; }
}

@media (prefers-reduced-motion: reduce) {
  .logout-dialog-backdrop,
  .logout-dialog { animation: none; }
}
</style>
