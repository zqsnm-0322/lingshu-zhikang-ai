<template>
  <main class="chronic-page">
    <header class="page-hero">
      <div>
        <button class="back-link" type="button" @click="router.back()">← 返回</button>
        <p class="eyebrow">个人健康监测</p>
        <h1>慢性病管理</h1>
        <p class="hero-copy">持续记录关键指标，及时发现变化，让每一次健康管理都有依据。</p>
      </div>
      <button class="primary-action" type="button" @click="openDialog">＋ 记录今日数据</button>
    </header>

    <section class="summary-grid">
      <article v-for="item in summaryCards" :key="item.label" class="summary-card">
        <div class="summary-icon" :class="item.tone">{{ item.icon }}</div>
        <div><strong :class="{ 'date-value': item.date }">{{ item.value }}</strong><span>{{ item.label }}</span></div>
        <small>{{ item.tip }}</small>
      </article>
    </section>

    <section class="metric-switcher">
      <button v-for="metric in indicatorTypes" :key="metric.id" type="button" class="metric-button" :class="{ active: currentType === metric.id }" @click="currentType = metric.id">
        <span class="metric-symbol" :style="{ background: metric.softColor, color: metric.color }">{{ metric.icon }}</span>
        <span><b>{{ metric.name }}</b><small>{{ metric.shortRange }}</small></span>
      </button>
    </section>

    <div class="content-grid">
      <section class="panel trend-panel">
        <div class="panel-heading">
          <div><p class="eyebrow">{{ currentMetric.name }}趋势</p><h2>{{ latestValue }}</h2><span class="value-unit">{{ recordList.length ? currentMetric.unit : '暂无记录' }}</span></div>
          <span class="range-badge">参考范围：{{ currentMetric.normalRange }}</span>
        </div>
        <div v-if="recordList.length" class="chart-area">
          <svg viewBox="0 0 640 210" role="img" :aria-label="`${currentMetric.name}趋势图`">
            <defs><linearGradient id="chartFill" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" :stop-color="currentMetric.color" stop-opacity=".28"/><stop offset="100%" :stop-color="currentMetric.color" stop-opacity="0"/></linearGradient></defs>
            <line v-for="y in [35,80,125,170]" :key="y" x1="20" :y1="y" x2="620" :y2="y" class="grid-line"/>
            <path :d="areaPath" fill="url(#chartFill)"/><path :d="linePath" fill="none" :stroke="currentMetric.color" stroke-width="5" stroke-linecap="round" stroke-linejoin="round"/>
            <circle v-for="point in chartPoints" :key="`${point.x}-${point.y}`" :cx="point.x" :cy="point.y" r="6" :fill="currentMetric.color" stroke="white" stroke-width="4"/>
          </svg>
          <div class="chart-labels"><span v-for="record in chartRecords" :key="record.id">{{ shortDate(record.date) }}</span></div>
        </div>
        <div v-else class="empty-chart"><div class="empty-illustration">⌁</div><h3>还没有{{ currentMetric.name }}记录</h3><p>记录后将在这里生成趋势，帮助你观察长期变化。</p><button type="button" @click="openDialog">添加第一条记录</button></div>
      </section>

      <aside class="panel insight-panel">
        <div class="panel-heading compact"><div><p class="eyebrow">健康提示</p><h2>今日建议</h2></div><span class="status-dot" :class="abnormalCount ? 'warning' : 'normal'">{{ abnormalCount ? '需关注' : '状态良好' }}</span></div>
        <div class="advice-list"><article v-for="advice in adviceList" :key="advice.title" class="advice-item"><span>{{ advice.icon }}</span><div><b>{{ advice.title }}</b><p>{{ advice.text }}</p></div></article></div>
        <p class="medical-note">页面数据仅用于日常健康管理，不能替代专业医疗诊断。</p>
      </aside>
    </div>

    <section class="panel records-panel">
      <div class="panel-heading records-heading"><div><p class="eyebrow">历史数据</p><h2>{{ currentMetric.name }}记录</h2></div><span>共 {{ recordList.length }} 条</span></div>
      <div v-if="recordList.length" class="record-table">
        <div class="record-row table-head"><span>记录时间</span><span>测量结果</span><span>状态</span><span>备注</span><span></span></div>
        <div v-for="record in recordList" :key="record.id" class="record-row">
          <span><b>{{ record.date }}</b><small>{{ record.time }}</small></span><span class="record-reading">{{ displayValue(record) }} <small>{{ currentMetric.unit }}</small></span>
          <span><i class="record-status" :class="record.isAbnormal ? 'abnormal' : 'normal'">{{ record.isAbnormal ? '异常' : '正常' }}</i></span><span class="notes">{{ record.notes || '—' }}</span>
          <span class="row-actions"><button type="button" @click="openDialog(record)">编辑</button><button class="delete-button" type="button" @click="deleteRecord(record.id)">删除</button></span>
        </div>
      </div>
      <div v-else class="empty-list">暂无历史记录</div>
    </section>

    <div v-if="showDialog" class="dialog-backdrop" @click.self="closeDialog">
      <section class="dialog" role="dialog" aria-modal="true">
        <header><div><p class="eyebrow">新增健康记录</p><h2>记录{{ currentMetric.name }}</h2></div><button type="button" @click="closeDialog">×</button></header>
        <div class="form-grid">
          <label><span>日期</span><input v-model="form.date" type="date"/></label><label><span>时间</span><input v-model="form.time" type="time"/></label>
          <template v-if="currentType === 'blood_pressure'"><label><span>收缩压 mmHg</span><input v-model.number="form.systolic" type="number" min="40" max="260" placeholder="例如 120"/></label><label><span>舒张压 mmHg</span><input v-model.number="form.diastolic" type="number" min="30" max="180" placeholder="例如 80"/></label></template>
          <template v-else><label><span>{{ currentMetric.name }} {{ currentMetric.unit }}</span><input v-model.number="form.value" type="number" min="0" step="0.1" :placeholder="currentMetric.placeholder"/></label><label v-if="currentType === 'blood_sugar'"><span>测量时段</span><select v-model="form.mealType"><option>空腹</option><option>餐后1小时</option><option>餐后2小时</option><option>睡前</option></select></label></template>
          <label class="full-width"><span>备注（选填）</span><textarea v-model.trim="form.notes" rows="3" placeholder="例如：晨起测量、服药后、运动后等"></textarea></label>
        </div>
        <footer><button class="cancel" type="button" @click="closeDialog">取消</button><button class="save" type="button" @click="saveRecord">保存记录</button></footer>
      </section>
    </div>
  </main>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { recordFeatureUsage } from '../utils/featureStats'

const router = useRouter()
const userId = Number(localStorage.getItem('userId') || 1)
const currentType = ref('blood_pressure')
const allRecords = ref([])
const showDialog = ref(false)
const form = ref({})
const indicatorTypes = [
  { id:'blood_pressure', name:'血压', icon:'♥', unit:'mmHg', color:'#ef5d5d', softColor:'#fff0f0', normalRange:'90–140 / 60–90', shortRange:'90–140 / 60–90' },
  { id:'blood_sugar', name:'血糖', icon:'◆', unit:'mmol/L', color:'#e9953f', softColor:'#fff5e9', normalRange:'空腹 3.9–6.1', shortRange:'空腹 3.9–6.1', placeholder:'例如 5.6' },
  { id:'weight', name:'体重', icon:'⚖', unit:'kg', color:'#4a9d78', softColor:'#ebf8f1', normalRange:'结合 BMI 综合判断', shortRange:'关注长期变化', placeholder:'例如 65.5' },
  { id:'heart_rate', name:'心率', icon:'⌁', unit:'次/分', color:'#6a72d8', softColor:'#f0f1ff', normalRange:'60–100', shortRange:'60–100 次/分', placeholder:'例如 72' }
]
const currentMetric = computed(() => indicatorTypes.find(item => item.id === currentType.value))
const recordList = computed(() => allRecords.value.filter(item => item.type === currentType.value).sort(sortByDate))
const abnormalCount = computed(() => allRecords.value.filter(item => item.isAbnormal).length)
const latest = computed(() => [...allRecords.value].sort(sortByDate)[0])
const latestValue = computed(() => recordList.value.length ? displayValue(recordList.value[0]) : '--')
const summaryCards = computed(() => [
  { icon:'▤', tone:'blue', value:allRecords.value.length, label:'累计记录', tip:allRecords.value.length ? '数据正在持续积累' : '从第一条记录开始' },
  { icon:'!', tone:'orange', value:abnormalCount.value, label:'异常记录', tip:abnormalCount.value ? '建议关注近期变化' : '暂无异常数据' },
  { icon:'✓', tone:'green', value:consecutiveDays(allRecords.value), label:'连续记录天数', tip:'坚持记录更容易看清趋势' },
  { icon:'◷', tone:'purple', value:latest.value?.date?.slice(5).replace('-', '/') || '暂无', label:'最近更新', tip:latest.value ? `${latest.value.time || ''} · ${indicatorTypes.find(i => i.id === latest.value.type)?.name || ''}` : '等待首次记录', date:true }
])
const chartRecords = computed(() => recordList.value.slice(0,7).reverse())
const chartValues = computed(() => chartRecords.value.map(record => currentType.value === 'blood_pressure' ? Number(record.systolic) : Number(record.value)))
const chartPoints = computed(() => { const values=chartValues.value;if(!values.length)return[];const min=Math.min(...values),max=Math.max(...values),spread=max-min||1;return values.map((value,index)=>({x:30+(index*580)/Math.max(values.length-1,1),y:175-((value-min)/spread)*125})) })
const linePath = computed(() => chartPoints.value.map((p,i)=>`${i?'L':'M'} ${p.x} ${p.y}`).join(' '))
const areaPath = computed(() => chartPoints.value.length ? `${linePath.value} L ${chartPoints.value.at(-1).x} 185 L ${chartPoints.value[0].x} 185 Z` : '')
const adviceList = computed(() => {
  const recent=recordList.value.slice(0,7), abnormal=recent.filter(i=>i.isAbnormal).length
  const base=abnormal>=3 ? {icon:'!',title:'近期波动较多',text:'最近记录中异常值较多，建议复测并咨询专业医生。'} : {icon:'✓',title:'保持规律记录',text:recent.length?'当前记录节奏良好，建议固定时间持续测量。':'添加第一条数据后，系统会为你生成趋势提示。'}
  const tips={blood_pressure:{icon:'○',title:'测量前先休息',text:'静坐 5 分钟后测量，手臂与心脏保持同一高度。'},blood_sugar:{icon:'◇',title:'标记测量时段',text:'区分空腹与餐后数据，避免将不同场景直接比较。'},weight:{icon:'↗',title:'关注长期趋势',text:'建议在相同时间和相似衣着条件下记录体重。'},heart_rate:{icon:'♥',title:'留意身体感受',text:'静息心率持续异常并伴随不适时，请及时就医。'}}
  return [base,tips[currentType.value]]
})
function sortByDate(a,b){return new Date(`${b.date}T${b.time||'00:00'}`)-new Date(`${a.date}T${a.time||'00:00'}`)}
function shortDate(date){return date?.slice(5).replace('-','/')||''}
function displayValue(record){return currentType.value==='blood_pressure'?`${record.systolic}/${record.diastolic}`:record.value}
function openDialog(record=null){const now=new Date();form.value=record?{...record}:{type:currentType.value,date:now.toISOString().slice(0,10),time:now.toTimeString().slice(0,5),systolic:'',diastolic:'',value:'',mealType:'空腹',notes:''};showDialog.value=true}
function closeDialog(){showDialog.value=false}
async function saveRecord(){const data=form.value;if(!data.date||!data.time)return ElMessage.warning('请选择记录日期和时间');if(data.type==='blood_pressure'&&(!data.systolic||!data.diastolic))return ElMessage.warning('请填写完整的血压数据');if(data.type!=='blood_pressure'&&(data.value===''||data.value==null))return ElMessage.warning(`请输入${currentMetric.value.name}数值`);try{const payload={...data,userId,isAbnormal:checkAbnormal(data)};const response=await axios[data.id?'put':'post'](data.id?'/api/chronic-disease/update':'/api/chronic-disease/add',payload);if(response.data?.code!==200)throw new Error(response.data?.message||'保存失败');await loadRecords();closeDialog();ElMessage.success(data.id?'记录已更新':'健康数据已记录');if(payload.isAbnormal)ElMessage.warning('本次数据超出参考范围，建议复测并持续关注')}catch(error){ElMessage.error(error.message||'保存失败')}}
function checkAbnormal(data){if(data.type==='blood_pressure')return +data.systolic<90||+data.systolic>140||+data.diastolic<60||+data.diastolic>90;if(data.type==='blood_sugar')return data.mealType==='空腹'?(+data.value<3.9||+data.value>6.1):data.mealType.includes('餐后')?+data.value>7.8:false;if(data.type==='heart_rate')return +data.value<60||+data.value>100;return false}
function deleteRecord(id){ElMessageBox.confirm('删除后无法恢复，确定继续吗？','删除记录',{confirmButtonText:'删除',cancelButtonText:'取消',type:'warning'}).then(async()=>{await axios.delete(`/api/chronic-disease/delete/${id}`,{params:{userId}});await loadRecords();ElMessage.success('记录已删除')}).catch(()=>{})}
async function loadRecords(){try{const response=await axios.get('/api/chronic-disease/all',{params:{userId}});allRecords.value=response.data?.data||[]}catch(error){ElMessage.error('健康记录加载失败')}}
function consecutiveDays(records){const dates=[...new Set(records.map(i=>i.date))].sort().reverse();if(!dates.length)return 0;let count=0;const today=new Date();today.setHours(0,0,0,0);for(const date of dates){if(Math.round((today-new Date(`${date}T00:00:00`))/86400000)!==count)break;count++}return count}
onMounted(()=>{loadRecords();recordFeatureUsage('慢性病管理')})
</script>

<style scoped lang="scss">
:global(body){background:#f4f7fb}*{box-sizing:border-box}.chronic-page{min-height:100vh;padding:28px clamp(18px,3vw,48px) 56px;color:#17243a;background:radial-gradient(circle at 95% 0%,#e9f5ff 0,transparent 28%),#f4f7fb}.page-hero{max-width:1460px;margin:0 auto 22px;padding:30px 34px;display:flex;align-items:flex-end;justify-content:space-between;gap:24px;color:white;border-radius:26px;background:linear-gradient(125deg,#173c73,#2465a7 58%,#2a8b9a);box-shadow:0 18px 45px rgba(31,76,127,.18)}.back-link{border:0;padding:0;margin-bottom:20px;background:transparent;color:#dcecff;cursor:pointer;font-size:14px}.eyebrow{margin:0 0 7px;color:#6f8097;font-size:12px;font-weight:800;letter-spacing:.14em}.page-hero .eyebrow{color:#b9d8ee}h1,h2,h3,p{margin-top:0}h1{margin-bottom:8px;font-size:clamp(28px,4vw,42px);letter-spacing:-.04em}.hero-copy{margin:0;color:#d7e7f4;font-size:15px}.primary-action{border:0;border-radius:14px;padding:14px 20px;color:#18436f;background:white;box-shadow:0 9px 25px rgba(8,37,70,.18);font-weight:800;cursor:pointer;white-space:nowrap}.summary-grid,.metric-switcher,.content-grid,.records-panel{max-width:1460px;margin-left:auto;margin-right:auto}.summary-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:16px;margin-bottom:18px}.summary-card,.panel,.metric-switcher{border:1px solid #e1e8f0;background:rgba(255,255,255,.96);box-shadow:0 8px 24px rgba(33,57,87,.05)}.summary-card{min-height:132px;padding:22px;border-radius:19px;display:grid;grid-template-columns:46px 1fr;align-items:center;gap:13px}.summary-card>div:nth-child(2){display:flex;flex-direction:column}.summary-card strong{font-size:28px;line-height:1.05}.summary-card strong.date-value{font-size:22px}.summary-card span,.summary-card small{color:#7f8da1}.summary-card small{grid-column:2;margin-top:-17px;font-size:12px}.summary-icon{width:46px;height:46px;border-radius:14px;display:grid;place-items:center;font-size:21px;font-weight:900}.blue{color:#2875c7;background:#e9f3ff}.orange{color:#d9822b;background:#fff2e4}.green{color:#2c9169;background:#e8f7f0}.purple{color:#6b64c8;background:#efedff}.metric-switcher{margin-bottom:18px;padding:8px;display:grid;grid-template-columns:repeat(4,1fr);gap:7px;border-radius:18px}.metric-button{border:0;padding:13px 16px;border-radius:13px;display:flex;align-items:center;gap:11px;text-align:left;color:#53647b;background:transparent;cursor:pointer}.metric-button:hover{background:#f5f8fc}.metric-button.active{color:#173c73;background:#eaf3fc;box-shadow:inset 0 0 0 1px #cfe0f2}.metric-symbol{width:36px;height:36px;border-radius:11px;display:grid;place-items:center;font-weight:800}.metric-button span:last-child{display:flex;flex-direction:column;gap:2px}.metric-button b{font-size:14px}.metric-button small{color:#8796a9;font-size:11px}.content-grid{display:grid;grid-template-columns:minmax(0,2fr) minmax(290px,.85fr);gap:18px;margin-bottom:18px}.panel{border-radius:21px;padding:25px}.panel-heading{display:flex;justify-content:space-between;gap:20px;align-items:flex-start;margin-bottom:20px}.panel-heading h2{display:inline;margin:0;font-size:25px}.value-unit{margin-left:8px;color:#8794a6}.range-badge{padding:8px 11px;border-radius:9px;background:#f2f6fa;color:#66778d;font-size:12px}.compact{align-items:center}.compact h2,.records-heading h2{font-size:20px}.chart-area{min-height:260px;padding:8px 5px 0}.chart-area svg{width:100%;height:218px}.grid-line{stroke:#edf1f5;stroke-width:1}.chart-labels{display:flex;justify-content:space-between;padding:0 3%;color:#8a98aa;font-size:11px}.empty-chart{min-height:250px;display:flex;flex-direction:column;align-items:center;justify-content:center;text-align:center;color:#74849a}.empty-chart h3{margin:5px 0}.empty-chart p{margin:0 0 17px}.empty-chart button{border:0;border-radius:10px;padding:10px 15px;background:#1f65a6;color:white;cursor:pointer}.empty-illustration{font-size:54px;color:#9bbbd6}.status-dot,.record-status{display:inline-flex;align-items:center;border-radius:999px;font-style:normal;font-size:11px;font-weight:800}.status-dot{padding:7px 10px}.normal{color:#237a58;background:#e9f7f1}.warning,.abnormal{color:#b34f37;background:#fff0ea}.advice-list{display:grid;gap:12px}.advice-item{display:flex;gap:12px;padding:15px;border-radius:14px;background:#f7f9fc}.advice-item>span{width:31px;height:31px;display:grid;place-items:center;border-radius:9px;background:white;color:#2d6aa5;font-weight:900}.advice-item b{font-size:13px}.advice-item p{margin:4px 0 0;color:#748399;font-size:12px;line-height:1.65}.medical-note{margin:18px 0 0;padding-top:15px;border-top:1px solid #edf0f4;color:#96a1af;font-size:11px;line-height:1.6}.records-heading{align-items:center}.records-heading>span{color:#8391a3;font-size:12px}.record-table{overflow:hidden;border:1px solid #e9edf2;border-radius:14px}.record-row{display:grid;grid-template-columns:1.2fr 1fr .65fr 1.8fr 58px;align-items:center;gap:14px;padding:14px 17px;border-top:1px solid #edf0f4;font-size:13px}.record-row:first-child{border-top:0}.record-row>span:first-child{display:flex;flex-direction:column}.record-row small{color:#8995a5}.table-head{color:#8290a2;background:#f7f9fb;font-size:11px}.record-reading{font-weight:800;font-size:15px}.record-status{width:max-content;padding:5px 9px}.notes{color:#6e7d90;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.delete-button{border:0;color:#a45c58;background:transparent;cursor:pointer;font-size:12px}.empty-list{text-align:center;padding:40px;color:#929eac}.dialog-backdrop{position:fixed;inset:0;z-index:1000;display:grid;place-items:center;padding:20px;background:rgba(17,32,53,.48);backdrop-filter:blur(5px)}.dialog{width:min(610px,100%);padding:25px;border-radius:22px;background:white;box-shadow:0 25px 70px rgba(10,28,51,.28)}.dialog header{display:flex;justify-content:space-between;align-items:start;margin-bottom:20px}.dialog h2{margin:0}.dialog header button{border:0;background:#f2f5f8;width:34px;height:34px;border-radius:10px;cursor:pointer;font-size:20px}.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:16px}.form-grid label{display:flex;flex-direction:column;gap:7px;color:#536277;font-size:12px;font-weight:700}.form-grid input,.form-grid select,.form-grid textarea{width:100%;border:1px solid #dbe3ec;border-radius:11px;padding:11px 12px;color:#26374d;background:white;outline:none;font:inherit}.form-grid input:focus,.form-grid select:focus,.form-grid textarea:focus{border-color:#4384bd;box-shadow:0 0 0 3px #e7f2fb}.full-width{grid-column:1/-1}.dialog footer{display:flex;justify-content:flex-end;gap:10px;margin-top:22px}.dialog footer button{border:0;border-radius:10px;padding:11px 18px;font-weight:700;cursor:pointer}.cancel{background:#eef2f6;color:#5e6b7d}.save{background:#1f65a6;color:white}@media(max-width:980px){.summary-grid{grid-template-columns:repeat(2,1fr)}.content-grid{grid-template-columns:1fr}.metric-switcher{grid-template-columns:repeat(2,1fr)}}@media(max-width:640px){.chronic-page{padding:14px 12px 36px}.page-hero{padding:23px;align-items:stretch;flex-direction:column}.primary-action{width:100%}.summary-grid{grid-template-columns:1fr 1fr;gap:10px}.summary-card{padding:15px;grid-template-columns:38px 1fr}.summary-icon{width:38px;height:38px}.summary-card small{display:none}.metric-switcher{grid-template-columns:1fr 1fr}.metric-button{padding:10px}.metric-button small{display:none}.panel{padding:18px}.panel-heading{flex-direction:column}.record-row{grid-template-columns:1.2fr 1fr .7fr 42px}.record-row .notes,.table-head span:nth-child(4){display:none}.form-grid{grid-template-columns:1fr}}
/* 与“病历分析”页面统一的医疗蓝视觉 */
:global(body){background:#eef9fe}.chronic-page{padding:30px;background:linear-gradient(135deg,#e8f7ff 0%,#f8fcff 48%,#eefbf8 100%)}
.page-hero{max-width:1520px;color:#243047;border:1px solid #d7eefb;background:rgba(255,255,255,.96);box-shadow:0 12px 30px rgba(45,126,164,.08)}
.page-hero .eyebrow{color:#0ea5e9}.page-hero .hero-copy{color:#6b7280}.back-link{color:#0ea5e9}.primary-action{color:#fff;background:#38bdf8;box-shadow:0 8px 20px rgba(56,189,248,.22)}
.summary-grid,.metric-switcher,.content-grid,.records-panel{max-width:1520px}.summary-card,.panel,.metric-switcher{border-color:#d7eefb;box-shadow:0 12px 30px rgba(45,126,164,.08)}
.blue,.orange,.green,.purple{color:#0ea5e9;background:#effaff}.metric-button.active{color:#0ea5e9;border-color:#aee7fb;background:#effaff}.metric-symbol{color:#0ea5e9;background:#effaff}
.range-badge{color:#557085;background:#effaff}.empty-chart button,.dialog footer .save{background:#38bdf8}.advice-item{background:#f8fcff}.advice-item>span{color:#0ea5e9}.record-table{border-color:#e0edf6}.table-head{background:#f8fcff}.row-actions{display:flex;gap:8px}.row-actions button{border:0;background:transparent;color:#0ea5e9;cursor:pointer;padding:0}
@media(max-width:640px){.chronic-page{padding:16px}.page-hero{padding:22px}}
</style>
