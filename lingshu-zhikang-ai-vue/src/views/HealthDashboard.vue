<template>
  <div class="medical-bi">
    <header class="topbar">
      <div class="brand">
        <div><small>MEDICAL HEALTH INTELLIGENCE</small><h1>灵枢智康健康数据 BI 中心</h1><p>Personal Health Analytics & Risk Monitoring</p></div>
      </div>
      <div class="online"><i></i>健康数据实时监测中</div>
      <div class="actions">
        <div class="clock"><span>{{ dateText }}</span><b>{{ timeText }}</b></div>
        <button @click="loadData">↻ 刷新数据</button><button @click="router.push('/home')">← 返回首页</button>
      </div>
    </header>

    <main>
      <section class="kpis">
        <article v-for="(item,i) in kpiItems" :key="item.label" :style="{ '--tone': item.color }">
          <div><em>0{{ i + 1 }}</em><span :class="item.state">{{ item.trend }}</span></div>
          <strong>{{ item.value }}<small>{{ item.unit }}</small></strong><p>{{ item.label }}</p><i><b :style="{ width: item.progress + '%' }"></b></i>
        </article>
      </section>

      <section class="dashboard">
        <article class="panel score-chart"><PanelTitle no="01" en="HEALTH SCORE" title="健康指数趋势" tag="近 7 天"/><div ref="scoreRef" class="chart"></div></article>

        <article class="panel portrait">
          <PanelTitle no="02" en="HEALTH PORTRAIT" title="综合健康画像" tag="实时评估"/>
          <div class="portrait-body">
            <div ref="radarRef" class="chart radar"></div>
            <div class="score-ring" :style="{ '--score': score }"><div><strong>{{ score }}</strong><span>综合评分</span></div></div>
            <div class="summary"><b :class="risk.tone">{{ risk.label }}</b><p>{{ risk.desc }}</p><small>数据完整度 {{ completeness }}%</small></div>
          </div>
        </article>

        <article class="panel risk"><PanelTitle no="03" en="RISK MATRIX" title="健康风险分层" tag="AI 分析"/><div ref="riskRef" class="chart"></div><div class="risk-key"><span><i class="ok"></i>低风险 {{ riskCounts.low }}</span><span><i class="warn"></i>需关注 {{ riskCounts.mid }}</span><span><i class="bad"></i>高风险 {{ riskCounts.high }}</span></div></article>

        <article class="panel symptoms"><PanelTitle no="04" en="SYMPTOM INSIGHT" title="症状关注度排行" tag="TOP 6"/><div ref="symptomRef" class="chart"></div></article>

        <article class="panel vitals">
          <div class="vital-head"><PanelTitle no="05" en="VITAL SIGNS" title="生命体征变化" tag="近 7 次"/><div class="tabs"><button v-for="tab in vitalTabs" :key="tab.key" :class="{active:vitalType===tab.key}" @click="vitalType=tab.key">{{ tab.label }}</button></div></div>
          <div ref="vitalRef" class="chart"></div>
        </article>

        <article class="panel indicators"><PanelTitle no="06" en="LATEST VITALS" title="最新健康指标" tag="今日"/><div class="indicator-grid"><div v-for="item in indicatorItems" :key="item.label" :class="item.status"><span>{{ item.label }}<i></i></span><strong>{{ item.value }}</strong><small>{{ item.note }}</small></div></div></article>

        <article class="panel lifestyle"><PanelTitle no="07" en="LIFESTYLE" title="健康行为分析" tag="近 7 天"/><div ref="lifeRef" class="chart"></div></article>

        <article class="panel events"><PanelTitle no="08" en="HEALTH EVENTS" title="最近健康动态" tag="实时"/><div class="event-list"><div v-for="event in events" :key="event.id"><time>{{ event.time }}</time><i :class="event.tone"></i><p><b>{{ event.title }}</b><small>{{ event.detail }}</small></p><span :class="event.tone">{{ event.status }}</span></div></div></article>
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed, defineComponent, h, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { recordFeatureUsage } from '../utils/featureStats'

const router=useRouter(), dateText=ref(''), timeText=ref(''), data=ref({healthScore:85,chronicRecords:[],diaries:[],reports:[],dailyStatistics:[]}), vitalType=ref('heart_rate')
const scoreRef=ref(),radarRef=ref(),riskRef=ref(),symptomRef=ref(),vitalRef=ref(),lifeRef=ref(),charts=new Map();let clockTimer,refreshTimer
const PanelTitle=defineComponent({props:{no:String,en:String,title:String,tag:String},setup:p=>()=>h('div',{class:'panel-title'},[h('div',[h('p',[h('b',p.no),` ${p.en}`]),h('h2',p.title)]),h('span',p.tag)])})
const vitalTabs=[{key:'blood_pressure',label:'血压'},{key:'blood_sugar',label:'血糖'},{key:'heart_rate',label:'心率'},{key:'weight',label:'体重'}]
const records=computed(()=>data.value.chronicRecords||[]),diaries=computed(()=>data.value.diaries||[]),reports=computed(()=>data.value.reports||[]),stats=computed(()=>data.value.dailyStatistics||[]),score=computed(()=>Number(data.value.healthScore||85))
const abnormal=computed(()=>records.value.filter(x=>x.isAbnormal).length),days=computed(()=>new Set(records.value.map(x=>x.date)).size),completeness=computed(()=>Math.min(100,52+days.value*5+Math.min(diaries.value.length,10)))
const kpiItems=computed(()=>[
  {label:'综合健康指数',value:score.value,unit:'分',trend:'↑ 3.2%',state:'up',color:'#1de9c1',progress:score.value},
  {label:'健康监测数据',value:records.value.length,unit:'项',trend:'↑ 12.5%',state:'up',color:'#24c9ff',progress:Math.min(100,records.value.length*3)},
  {label:'连续监测天数',value:days.value,unit:'天',trend:'连续记录',state:'flat',color:'#4f8cff',progress:Math.min(100,days.value*12)},
  {label:'异常指标提醒',value:abnormal.value,unit:'次',trend:abnormal.value?'需关注':'状态稳定',state:abnormal.value?'warning':'up',color:'#ffbd4a',progress:Math.min(100,abnormal.value*24)},
  {label:'症状评估报告',value:reports.value.length,unit:'份',trend:'AI 已分析',state:'flat',color:'#a875ff',progress:Math.min(100,reports.value.length*15)},
  {label:'健康日记完成率',value:Math.min(100,Math.round(diaries.value.length/7*100)),unit:'%',trend:'↑ 8.6%',state:'up',color:'#ff6f91',progress:Math.min(100,diaries.value.length/7*100)}])
const riskCounts=computed(()=>{const high=reports.value.filter(x=>['HIGH','URGENT'].includes(x.urgencyLevel)).length,mid=reports.value.filter(x=>x.urgencyLevel==='MEDIUM').length+abnormal.value;return{high,mid,low:Math.max(1,reports.value.length-high-reports.value.filter(x=>x.urgencyLevel==='MEDIUM').length)}})
const risk=computed(()=>riskCounts.value.high?{label:'重点关注',tone:'bad',desc:'存在高风险信号，建议及时咨询专业医生。'}:riskCounts.value.mid>2?{label:'持续观察',tone:'warn',desc:'部分指标出现波动，请保持规律监测。'}:{label:'健康状态良好',tone:'ok',desc:'核心指标整体稳定，请继续保持健康生活方式。'})
const latest=computed(()=>{const out={};records.value.forEach(x=>{if(!out[x.type])out[x.type]=x});return out})
const indicatorItems=computed(()=>{const x=latest.value,s=diaries.value[0]?.sleep;return[
  {label:'血压',value:x.blood_pressure?`${x.blood_pressure.systolic}/${x.blood_pressure.diastolic} mmHg`:'--',note:'参考 90–139 / 60–89',status:x.blood_pressure?.isAbnormal?'warn':'ok'},
  {label:'空腹血糖',value:x.blood_sugar?`${x.blood_sugar.value} mmol/L`:'--',note:'参考 3.9–6.1',status:x.blood_sugar?.isAbnormal?'warn':'ok'},
  {label:'静息心率',value:x.heart_rate?`${x.heart_rate.value} 次/分`:'--',note:'参考 60–100',status:x.heart_rate?.isAbnormal?'warn':'ok'},
  {label:'体重',value:x.weight?`${x.weight.value} kg`:'--',note:'连续趋势监测',status:'ok'},
  {label:'昨夜睡眠',value:s?`${s} 小时`:'--',note:'建议 7–9 小时',status:s&&s<7?'warn':'ok'},
  {label:'数据完整度',value:`${completeness.value}%`,note:'健康画像覆盖率',status:completeness.value>80?'ok':'warn'}]})
const events=computed(()=>[...records.value.slice(0,4).map(x=>({id:`v${x.id}`,date:x.createTime||x.date,title:typeName(x.type),detail:x.type==='blood_pressure'?`${x.systolic}/${x.diastolic} mmHg`:`${x.value} ${unit(x.type)}`,status:x.isAbnormal?'需关注':'正常',tone:x.isAbnormal?'warn':'ok'})),...reports.value.slice(0,2).map(x=>({id:`r${x.id}`,date:x.createdAt,title:'AI 症状评估',detail:x.symptoms,status:x.urgencyLevel==='MEDIUM'?'需关注':'低风险',tone:x.urgencyLevel==='MEDIUM'?'warn':'ok'})),...diaries.value.slice(0,2).map(x=>({id:`d${x.id}`,date:x.createTime||x.date,title:'健康日记',detail:`睡眠 ${x.sleep}h · 心情 ${x.mood}/5`,status:'已记录',tone:'info'}))].sort((a,b)=>new Date(b.date)-new Date(a.date)).slice(0,7).map(x=>({...x,time:timeOf(x.date)})))

const loadData=async()=>{try{const id=localStorage.getItem('userId')||'5',res=await fetch(`/api/health-dashboard?userId=${encodeURIComponent(id)}`);if(res.ok){const json=await res.json();if(json.code===200||json.code==='200')data.value={...data.value,...json.data}}}catch{}await nextTick();renderCharts()}
const updateClock=()=>{const n=new Date(),w=['星期日','星期一','星期二','星期三','星期四','星期五','星期六'];dateText.value=`${n.getFullYear()}年${String(n.getMonth()+1).padStart(2,'0')}月${String(n.getDate()).padStart(2,'0')}日 ${w[n.getDay()]}`;timeText.value=n.toLocaleTimeString('zh-CN',{hour12:false})}
const chart=(key,el)=>{if(!el)return;if(!charts.has(key))charts.set(key,echarts.init(el));return charts.get(key)},muted='#789eba',grid='rgba(91,163,210,.12)',tip={backgroundColor:'rgba(3,18,45,.96)',borderColor:'#1b9ed0',textStyle:{color:'#e8f7ff'}}
const renderCharts=()=>{renderScore();renderRadar();renderRisk();renderSymptoms();renderVital();renderLife()}
const renderScore=()=>{const c=chart('score',scoreRef.value);if(!c)return;const src=stats.value.length?[...stats.value].reverse():[82,85,84,89,91,93,score.value].map((healthScore,i)=>({statDate:`2026-09-${8+i}`,healthScore}));c.setOption({tooltip:{...tip,trigger:'axis'},grid:{left:46,right:18,top:24,bottom:42,containLabel:false},xAxis:{type:'category',boundaryGap:false,data:src.map(x=>shortDate(x.statDate)),axisLabel:{color:muted,margin:12},axisLine:{lineStyle:{color:grid}}},yAxis:{type:'value',min:60,max:100,axisLabel:{color:muted},splitLine:{lineStyle:{color:grid,type:'dashed'}}},series:[{type:'line',smooth:true,data:src.map(x=>x.healthScore),symbolSize:7,lineStyle:{color:'#1de9c1',width:3},itemStyle:{color:'#1de9c1',borderColor:'#fff',borderWidth:2},areaStyle:{color:new echarts.graphic.LinearGradient(0,0,0,1,[{offset:0,color:'rgba(29,233,193,.35)'},{offset:1,color:'rgba(29,233,193,0)'}])}}]},true)}
const renderRadar=()=>{const c=chart('radar',radarRef.value);if(!c)return;c.setOption({radar:{radius:'67%',center:['50%','52%'],splitNumber:4,indicator:['生命体征','睡眠','运动','饮食','情绪'].map(name=>({name,max:100})),axisName:{color:'#8fb7d2',fontSize:11},splitArea:{areaStyle:{color:['rgba(19,70,115,.08)','rgba(19,70,115,.18)']}},splitLine:{lineStyle:{color:'rgba(32,184,224,.22)'}},axisLine:{lineStyle:{color:'rgba(32,184,224,.18)'}}},series:[{type:'radar',data:[{value:[92,Math.min(100,(diaries.value[0]?.sleep||7)*12),76,86,88]}],symbolSize:4,lineStyle:{color:'#24c9ff',width:2},itemStyle:{color:'#1de9c1'},areaStyle:{color:'rgba(36,201,255,.24)'}}]},true)}
const renderRisk=()=>{const c=chart('risk',riskRef.value);if(!c)return;const r=riskCounts.value;c.setOption({tooltip:{...tip,trigger:'item'},series:[{type:'pie',radius:['56%','77%'],center:['50%','47%'],label:{show:false},itemStyle:{borderColor:'#ffffff',borderWidth:4},data:[{name:'低风险',value:r.low,itemStyle:{color:'#08b895'}},{name:'需关注',value:r.mid,itemStyle:{color:'#f2aa32'}},{name:'高风险',value:r.high,itemStyle:{color:'#ef5b76'}}]}],graphic:[{type:'text',left:'center',top:'36%',style:{text:`${r.low+r.mid+r.high}`,fill:'#17243a',font:'700 29px Inter'}},{type:'text',left:'center',top:'53%',style:{text:'评估项',fill:muted,font:'11px Inter'}}]},true)}
const renderSymptoms=()=>{const c=chart('symptoms',symptomRef.value);if(!c)return;const count={};reports.value.forEach(x=>String(x.symptoms||'').split(/[、,，\s]+/).filter(Boolean).forEach(s=>count[s]=(count[s]||0)+1));const d=Object.entries(count).sort((a,b)=>b[1]-a[1]).slice(0,6);c.setOption({tooltip:{...tip,trigger:'axis',axisPointer:{type:'shadow'}},grid:{left:58,right:18,top:14,bottom:20},xAxis:{type:'value',axisLabel:{color:muted},splitLine:{lineStyle:{color:grid}}},yAxis:{type:'category',inverse:true,data:d.map(x=>x[0]),axisLabel:{color:'#b6d5e6'},axisLine:{show:false},axisTick:{show:false}},series:[{type:'bar',data:d.map(x=>x[1]),barWidth:10,showBackground:true,backgroundStyle:{color:'rgba(65,130,170,.09)',borderRadius:8},itemStyle:{borderRadius:8,color:new echarts.graphic.LinearGradient(0,0,1,0,[{offset:0,color:'#327cff'},{offset:1,color:'#1de9c1'}])}}]},true)}
const renderVital=()=>{const c=chart('vital',vitalRef.value);if(!c)return;const d=records.value.filter(x=>x.type===vitalType.value).slice(0,7).reverse(),bp=vitalType.value==='blood_pressure',sets=bp?[{name:'收缩压',data:d.map(x=>x.systolic),color:'#24c9ff'},{name:'舒张压',data:d.map(x=>x.diastolic),color:'#1de9c1'}]:[{name:vitalTabs.find(x=>x.key===vitalType.value)?.label,data:d.map(x=>x.value),color:'#24c9ff'}];c.setOption({tooltip:{...tip,trigger:'axis'},legend:{top:4,right:18,textStyle:{color:muted}},grid:{left:48,right:24,top:42,bottom:40},xAxis:{type:'category',boundaryGap:false,data:d.map(x=>shortDate(x.date)),axisLabel:{color:muted,margin:11},axisLine:{lineStyle:{color:grid}}},yAxis:{type:'value',scale:true,axisLabel:{color:muted},splitLine:{lineStyle:{color:grid,type:'dashed'}}},series:sets.map(s=>({name:s.name,type:'line',smooth:true,data:s.data,symbolSize:7,lineStyle:{color:s.color,width:3},itemStyle:{color:s.color,borderColor:'#fff',borderWidth:2},areaStyle:{color:new echarts.graphic.LinearGradient(0,0,0,1,[{offset:0,color:`${s.color}45`},{offset:1,color:`${s.color}05`}])}}))},true)}
const renderLife=()=>{const c=chart('life',lifeRef.value);if(!c)return;const d=diaries.value.slice(0,7).reverse();c.setOption({tooltip:{...tip,trigger:'axis'},legend:{top:2,right:18,textStyle:{color:muted}},grid:{left:42,right:24,top:36,bottom:24},xAxis:{type:'category',data:d.map(x=>shortDate(x.date)),axisLabel:{color:muted},axisLine:{lineStyle:{color:grid}}},yAxis:[{type:'value',max:10,axisLabel:{color:muted},splitLine:{lineStyle:{color:grid,type:'dashed'}}},{type:'value',max:5,show:false}],series:[{name:'睡眠时长',type:'bar',barWidth:14,data:d.map(x=>x.sleep),itemStyle:{borderRadius:[5,5,0,0],color:new echarts.graphic.LinearGradient(0,0,0,1,[{offset:0,color:'#24c9ff'},{offset:1,color:'rgba(36,201,255,.12)'}])}},{name:'心情指数',type:'line',yAxisIndex:1,smooth:true,data:d.map(x=>x.mood),symbolSize:7,lineStyle:{color:'#ff6f91',width:2},itemStyle:{color:'#ff6f91'}}]},true)}
const shortDate=v=>{const d=new Date(v);return Number.isNaN(d.getTime())?String(v).slice(5):`${d.getMonth()+1}/${d.getDate()}`},timeOf=v=>{const d=new Date(v);return Number.isNaN(d.getTime())?'--:--':`${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`},typeName=t=>({blood_pressure:'血压监测',blood_sugar:'血糖监测',heart_rate:'心率监测',weight:'体重监测'})[t]||'健康指标',unit=t=>({blood_sugar:'mmol/L',heart_rate:'次/分',weight:'kg'})[t]||''
const resize=()=>charts.forEach(c=>c.resize());watch(vitalType,()=>nextTick(renderVital))
onMounted(async()=>{updateClock();clockTimer=setInterval(updateClock,1000);await loadData();refreshTimer=setInterval(loadData,30000);window.addEventListener('resize',resize);recordFeatureUsage('医疗健康数据BI中心')})
onUnmounted(()=>{clearInterval(clockTimer);clearInterval(refreshTimer);window.removeEventListener('resize',resize);charts.forEach(c=>c.dispose())})
</script>

<style scoped lang="scss">
.medical-bi{--cyan:#24c9ff;--mint:#1de9c1;--text:#e8f7ff;--muted:#789eba;min-height:100vh;overflow:hidden;color:var(--text);font-family:Inter,"Noto Sans SC","Microsoft YaHei",sans-serif;background:radial-gradient(circle at 50% -12%,#0b3968 0,transparent 31%),linear-gradient(rgba(45,135,190,.05) 1px,transparent 1px),linear-gradient(90deg,rgba(45,135,190,.05) 1px,transparent 1px),linear-gradient(145deg,#030b1d,#061a39 52%,#052849);background-size:auto,34px 34px,34px 34px,auto}.topbar{height:116px;padding:16px 22px;display:flex;align-items:center;justify-content:space-between;gap:22px;background:rgba(3,12,31,.91);border-bottom:1px solid rgba(36,201,255,.35);position:relative}.topbar:after{content:'';position:absolute;left:50%;bottom:-2px;width:64%;height:3px;transform:translateX(-50%);background:linear-gradient(90deg,transparent,var(--cyan),transparent);filter:drop-shadow(0 0 7px var(--cyan))}.brand{display:flex;align-items:center;gap:16px;min-width:460px}.brand h1,.brand p,.brand small{margin:0}.brand small{color:#77bfde;font-size:10px;letter-spacing:.25em}.brand h1{margin:5px 0 3px;font-size:28px;letter-spacing:.06em;background:linear-gradient(90deg,#38f2d4,#18d5ff);background-clip:text;-webkit-background-clip:text;-webkit-text-fill-color:transparent;filter:drop-shadow(0 0 10px rgba(29,233,193,.2))}.brand p{color:#5f89a7;font-size:11px;letter-spacing:.16em}.cross{width:60px;height:60px;position:relative;flex:0 0 auto;border:1px solid rgba(36,201,255,.5);border-radius:17px;background:rgba(16,74,108,.18);box-shadow:inset 0 0 20px rgba(36,201,255,.08)}.cross:after{content:'';position:absolute;inset:10px;border:1px solid rgba(29,233,193,.24);border-radius:50%}.cross i{position:absolute;z-index:1;left:50%;top:50%;width:27px;height:2px;transform:translate(-50%,-50%);background:var(--mint);box-shadow:0 0 8px var(--mint)}.cross i:last-child{transform:translate(-50%,-50%) rotate(90deg)}.online{display:flex;align-items:center;gap:8px;color:#9bcce2;font-size:12px;white-space:nowrap}.online i{width:7px;height:7px;border-radius:50%;background:var(--mint);box-shadow:0 0 0 5px rgba(29,233,193,.09),0 0 12px var(--mint);animation:pulse 1.8s infinite}.actions{display:flex;align-items:center;gap:10px}.clock{min-width:210px;padding:9px 15px;text-align:center;border:1px solid rgba(36,201,255,.28);border-radius:9px;background:linear-gradient(135deg,rgba(15,59,110,.75),rgba(5,31,70,.72))}.clock span,.clock b{display:block}.clock span{color:#a6c8dc;font-size:11px}.clock b{margin-top:3px;color:var(--cyan);font:400 25px Consolas;letter-spacing:.12em;text-shadow:0 0 12px rgba(36,201,255,.38)}.actions button{height:46px;padding:0 15px;border:1px solid rgba(36,201,255,.45)!important;border-radius:7px!important;background:rgba(8,57,94,.44)!important;color:#67ddff!important;font-weight:700;cursor:pointer}.actions button:hover{border-color:var(--mint)!important;color:#fff!important;box-shadow:0 0 17px rgba(36,201,255,.16)!important}main{padding:12px}.kpis{display:grid;grid-template-columns:repeat(6,1fr);gap:9px;margin-bottom:10px}.kpis article{--tone:#24c9ff;height:111px;padding:11px 13px;position:relative;overflow:hidden;border:1px solid color-mix(in srgb,var(--tone) 32%,transparent);border-radius:8px;background:linear-gradient(135deg,rgba(12,45,88,.93),rgba(7,28,61,.93));box-shadow:inset 3px 0 var(--tone),0 8px 22px rgba(0,5,22,.2)}.kpis article:after{content:'';position:absolute;right:-30px;bottom:-55px;width:110px;height:110px;border:1px solid color-mix(in srgb,var(--tone) 16%,transparent);border-radius:50%}.kpis article>div{display:flex;justify-content:space-between}.kpis em{color:var(--tone);font:700 9px Consolas;font-style:normal}.kpis article>div span{font-size:9px;color:#6e95ae}.kpis article>div span.up{color:#33dfa3}.kpis article>div span.warning{color:#ffbd4a}.kpis article>strong{display:block;margin-top:5px;color:#f5fbff;font:700 25px Consolas;text-shadow:0 0 12px color-mix(in srgb,var(--tone) 30%,transparent)}.kpis article>strong small{margin-left:4px;color:#88a9bd;font:10px Inter}.kpis article>p{margin:0 0 7px;color:#91b3c8;font-size:11px}.kpis article>i{display:block;height:3px;overflow:hidden;border-radius:4px;background:rgba(91,145,180,.12)}.kpis article>i b{display:block;height:100%;background:linear-gradient(90deg,transparent,var(--tone));box-shadow:0 0 7px var(--tone)}.dashboard{display:grid;grid-template-columns:1fr 1.55fr 1fr;grid-template-rows:252px 285px 235px;gap:10px}.panel{position:relative;min-width:0;min-height:0;padding:13px 14px;overflow:hidden;border:1px solid rgba(40,161,215,.3)!important;border-radius:8px!important;background:linear-gradient(145deg,rgba(7,31,69,.95),rgba(5,24,55,.93))!important;box-shadow:inset 0 0 32px rgba(36,201,255,.018),0 10px 25px rgba(0,5,22,.22)!important}.panel:before{content:'';position:absolute;inset:0;pointer-events:none;background:linear-gradient(var(--cyan),var(--cyan)) left top/20px 2px no-repeat,linear-gradient(var(--cyan),var(--cyan)) left top/2px 20px no-repeat,linear-gradient(var(--mint),var(--mint)) right bottom/20px 2px no-repeat,linear-gradient(var(--mint),var(--mint)) right bottom/2px 20px no-repeat;filter:drop-shadow(0 0 5px rgba(36,201,255,.45))}.score-chart{grid-area:1/1}.portrait{grid-area:1/2}.risk{grid-area:1/3}.symptoms{grid-area:2/1}.vitals{grid-area:2/2}.indicators{grid-area:2/3}.lifestyle{grid-area:3/1/3/3}.events{grid-area:3/3}.panel-title{position:relative;z-index:2;display:flex;justify-content:space-between;padding-bottom:8px;border-bottom:1px solid rgba(69,148,193,.14)}.panel-title p,.panel-title h2{margin:0}.panel-title p{color:#6fbbdb;font-size:9px;font-weight:800;letter-spacing:.13em}.panel-title p b{color:var(--mint);margin-right:4px}.panel-title h2{margin-top:3px;color:#e5f5fb;font-size:16px}.panel-title>span{height:20px;padding:3px 7px;border:1px solid rgba(36,201,255,.2);border-radius:4px;color:#6fa6c2;font-size:8px;background:rgba(36,201,255,.04)}.chart{width:100%;height:calc(100% - 43px)}.portrait-body{height:calc(100% - 42px);display:grid;grid-template-columns:1fr 130px 1fr;align-items:center}.radar{height:100%}.score-ring{width:112px;height:112px;margin:auto;display:grid;place-items:center;border-radius:50%;background:radial-gradient(circle,#071c42 48%,transparent 50%),conic-gradient(var(--mint) calc(var(--score)*1%),rgba(36,201,255,.12) 0);box-shadow:0 0 25px rgba(29,233,193,.16);position:relative}.score-ring:before{content:'';position:absolute;inset:-9px;border:1px dashed rgba(36,201,255,.2);border-radius:50%;animation:spin 18s linear infinite}.score-ring strong,.score-ring span{display:block;text-align:center}.score-ring strong{font:700 35px Consolas}.score-ring span{color:#789eba;font-size:9px}.summary b{display:inline-block;padding:4px 9px;border-radius:20px;font-size:11px}.summary b.ok{color:#1de9c1;border:1px solid rgba(29,233,193,.3);background:rgba(29,233,193,.08)}.summary b.warn{color:#ffbd4a;border:1px solid rgba(255,189,74,.3);background:rgba(255,189,74,.08)}.summary b.bad{color:#ff617d;border:1px solid rgba(255,97,125,.3);background:rgba(255,97,125,.08)}.summary p{margin:8px 0;color:#88abc0;font-size:10px;line-height:1.5}.summary small{color:#557d98;font-size:9px}.risk .chart{height:148px}.risk-key{display:flex;justify-content:center;gap:10px;color:#7f9fb4;font-size:9px}.risk-key span{display:flex;align-items:center;gap:4px}.risk-key i{width:6px;height:6px;border-radius:50%}.ok i,.risk-key .ok{background:#1de9c1}.warn i,.risk-key .warn{background:#ffbd4a}.bad i,.risk-key .bad{background:#ff617d}.vital-head{display:flex;align-items:flex-start}.vital-head>.panel-title{flex:1}.tabs{display:flex;gap:4px;margin-left:10px}.tabs button{height:29px;padding:0 10px;border:1px solid rgba(60,137,190,.25)!important;border-radius:4px!important;background:rgba(7,29,64,.6)!important;color:#7097b3!important;font-size:10px;cursor:pointer}.tabs button.active{border-color:var(--cyan)!important;color:var(--mint)!important;background:rgba(29,189,202,.1)!important;box-shadow:inset 0 -2px var(--mint)!important}.vitals>.chart{height:calc(100% - 44px)}.indicator-grid{display:grid;grid-template-columns:1fr 1fr;gap:7px;margin-top:10px}.indicator-grid>div{padding:8px 9px;border:1px solid rgba(62,137,180,.19);border-radius:5px;background:rgba(15,52,91,.34)}.indicator-grid span{display:flex;justify-content:space-between;color:#85a9bf;font-size:9px}.indicator-grid span i{width:5px;height:5px;border-radius:50%;background:#1de9c1;box-shadow:0 0 7px #1de9c1}.indicator-grid .warn span i{background:#ffbd4a;box-shadow:0 0 7px #ffbd4a}.indicator-grid strong,.indicator-grid small{display:block}.indicator-grid strong{margin-top:4px;color:#e6f7ff;font-size:12px}.indicator-grid small{margin-top:2px;color:#527892;font-size:8px}.event-list{height:calc(100% - 45px);overflow:auto;scrollbar-width:thin;scrollbar-color:rgba(36,201,255,.3) transparent}.event-list>div{display:grid;grid-template-columns:34px 7px 1fr auto;gap:7px;align-items:center;padding:6px 1px;border-bottom:1px solid rgba(68,137,180,.1)}.event-list time{color:#578099;font:9px Consolas}.event-list>div>i{width:5px;height:5px;border-radius:50%}.event-list>div>i.ok{background:#1de9c1;box-shadow:0 0 6px #1de9c1}.event-list>div>i.warn{background:#ffbd4a;box-shadow:0 0 6px #ffbd4a}.event-list>div>i.info{background:#24c9ff;box-shadow:0 0 6px #24c9ff}.event-list p{min-width:0;margin:0}.event-list p b,.event-list p small{display:block;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}.event-list p b{color:#cde7f3;font-size:10px}.event-list p small{color:#5c8198;font-size:8px}.event-list>div>span{padding:2px 4px;border-radius:3px;font-size:8px}.event-list span.ok{color:#1de9c1;background:rgba(29,233,193,.08)}.event-list span.warn{color:#ffbd4a;background:rgba(255,189,74,.08)}.event-list span.info{color:#24c9ff;background:rgba(36,201,255,.08)}@keyframes pulse{50%{opacity:.45;transform:scale(.82)}}@keyframes spin{to{transform:rotate(360deg)}}
@media(max-width:1350px){.online{display:none}.brand{min-width:0}.kpis{grid-template-columns:repeat(3,1fr)}.dashboard{grid-template-columns:1fr 1fr;grid-template-rows:280px 280px 300px 270px}.portrait{grid-area:1/1/1/3}.score-chart{grid-area:2/1}.risk{grid-area:2/2}.symptoms{grid-area:3/1}.indicators{grid-area:3/2}.vitals{grid-area:4/1/4/3}.lifestyle,.events{min-height:250px;grid-column:1/3;grid-row:auto}}
@media(max-width:780px){.medical-bi{overflow:auto}.topbar{height:auto;flex-direction:column;align-items:flex-start}.actions{width:100%;flex-wrap:wrap}.clock{width:100%}.actions button{flex:1}.brand h1{font-size:21px}.brand small,.brand p{display:none}.kpis{grid-template-columns:1fr 1fr}.dashboard{display:flex;flex-direction:column}.panel{min-height:285px}.portrait{min-height:430px}.portrait-body{grid-template-columns:1fr;grid-template-rows:160px 130px auto}.radar{height:160px}.summary{text-align:center}.vital-head{display:block}.tabs{margin:7px 0}.indicator-grid{grid-template-columns:1fr}.indicators{min-height:480px}}
@media(prefers-reduced-motion:reduce){.online i,.score-ring:before{animation:none}}

/* Deep violet clinical theme */
.medical-bi {
  --cyan: #45d7ff;
  --mint: #32e6bf;
  --text: #f1f4ff;
  --muted: #9aa6ca;
  background:
    radial-gradient(circle at 14% 12%, rgba(102, 73, 211, .24), transparent 30%),
    radial-gradient(circle at 86% 18%, rgba(20, 175, 194, .14), transparent 27%),
    radial-gradient(circle at 52% 78%, rgba(76, 55, 170, .12), transparent 35%),
    linear-gradient(rgba(140, 126, 211, .045) 1px, transparent 1px),
    linear-gradient(90deg, rgba(140, 126, 211, .045) 1px, transparent 1px),
    linear-gradient(145deg, #090719 0%, #11102d 48%, #101b35 100%);
  background-size: auto, auto, auto, 34px 34px, 34px 34px, auto;
}

.topbar {
  background: linear-gradient(110deg, rgba(10, 7, 28, .96), rgba(17, 14, 46, .94), rgba(8, 25, 47, .94));
  border-bottom-color: rgba(91, 188, 234, .35);
  box-shadow: 0 12px 34px rgba(2, 1, 14, .28);
}

.clock {
  background: linear-gradient(135deg, rgba(50, 37, 108, .78), rgba(17, 33, 70, .8));
  border-color: rgba(104, 173, 236, .34);
}

.actions button {
  background: linear-gradient(180deg, rgba(50, 41, 108, .58), rgba(24, 31, 73, .58)) !important;
  border-color: rgba(86, 184, 231, .46) !important;
}

.kpis article {
  background: linear-gradient(135deg, rgba(33, 29, 78, .94), rgba(18, 24, 59, .94));
  box-shadow: inset 3px 0 var(--tone), 0 10px 26px rgba(3, 1, 20, .28);
}

.panel {
  border-color: rgba(92, 135, 205, .32) !important;
  background:
    linear-gradient(145deg, rgba(28, 25, 70, .95), rgba(15, 23, 55, .94)) !important;
  box-shadow: inset 0 0 34px rgba(110, 88, 220, .035), 0 12px 28px rgba(3, 1, 20, .3) !important;
}

.panel-title {
  border-bottom-color: rgba(125, 132, 205, .16);
}

.panel-title h2 {
  color: #edf2ff !important;
  text-shadow: 0 0 15px rgba(112, 193, 255, .12);
}

.panel-title p { color: #8ebada !important; }
.panel-title > span { color: #b5c5e8; background: rgba(91, 91, 180, .1); }

.indicator-grid > div {
  background: linear-gradient(135deg, rgba(47, 42, 101, .38), rgba(24, 34, 72, .38));
  border-color: rgba(104, 130, 199, .23);
}

.tabs button {
  background: rgba(28, 29, 71, .72) !important;
  border-color: rgba(104, 130, 199, .3) !important;
}

.score-ring {
  background: radial-gradient(circle, #17163c 48%, transparent 50%), conic-gradient(var(--mint) calc(var(--score) * 1%), rgba(112, 99, 202, .18) 0);
}

/* Clean white clinical theme */
.medical-bi {
  --cyan: #089cca;
  --mint: #08b895;
  --text: #17243a;
  --muted: #6c7f99;
  color: var(--text);
  background:
    radial-gradient(circle at 12% 8%, rgba(45, 189, 224, .13), transparent 27%),
    radial-gradient(circle at 88% 16%, rgba(62, 113, 214, .08), transparent 25%),
    linear-gradient(rgba(35, 123, 170, .045) 1px, transparent 1px),
    linear-gradient(90deg, rgba(35, 123, 170, .045) 1px, transparent 1px),
    linear-gradient(145deg, #f8fcff 0%, #eef6fb 52%, #f7fbfe 100%);
  background-size: auto, auto, 34px 34px, 34px 34px, auto;
}

.topbar {
  background: rgba(255, 255, 255, .94);
  border-bottom-color: rgba(8, 156, 202, .25);
  box-shadow: 0 10px 30px rgba(38, 88, 122, .09);
}

.brand h1 {
  background: linear-gradient(90deg, #078eab, #08b895);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  filter: none;
}

.brand small { color: #4388a5; }
.brand p { color: #7893a7; }
.cross { background: #f4fbfd; box-shadow: inset 0 0 18px rgba(8, 156, 202, .06), 0 8px 22px rgba(31, 111, 147, .08); }
.online { color: #526f83; }

.clock {
  background: linear-gradient(135deg, #f4faff, #edf7fa);
  border-color: rgba(8, 156, 202, .24);
}
.clock span { color: #55748b; }
.clock b { color: #087fa7; text-shadow: none; }

.actions button {
  background: #ffffff !important;
  border-color: rgba(8, 156, 202, .32) !important;
  color: #087fa7 !important;
  box-shadow: 0 5px 15px rgba(34, 102, 137, .07) !important;
}
.actions button:hover { background: #effbff !important; color: #066f92 !important; }

.kpis article {
  background: rgba(255, 255, 255, .94);
  box-shadow: inset 3px 0 var(--tone), 0 9px 22px rgba(41, 88, 119, .08);
}
.kpis article > strong { color: #17243a; text-shadow: none; }
.kpis article > strong small { color: #71859a; }
.kpis article > p { color: #667b91; }
.kpis article > div span { color: #8193a5; }
.kpis article > i { background: #eaf1f5; }

.panel {
  border-color: rgba(54, 132, 174, .2) !important;
  background: rgba(255, 255, 255, .95) !important;
  box-shadow: 0 10px 28px rgba(41, 88, 119, .08) !important;
}
.panel::before { opacity: .68; }
.panel-title { border-bottom-color: #e8eff4; }
.panel-title h2 { color: #1c2d44 !important; text-shadow: none; }
.panel-title p { color: #5c8da7 !important; }
.panel-title > span { color: #608198; background: #f1f7fa; border-color: #dbeaf1; }

.portrait-body,
.chart { color: #1c2d44; }
.score-ring {
  color: #17243a;
  background: radial-gradient(circle, #ffffff 48%, transparent 50%), conic-gradient(var(--mint) calc(var(--score) * 1%), #dbecef 0);
  box-shadow: 0 9px 25px rgba(8, 184, 149, .13);
}
.score-ring::before { border-color: rgba(8, 156, 202, .24); }
.score-ring strong { color: #17243a; }
.score-ring span { color: #71859a; }
.summary p { color: #5f7489; }
.summary small { color: #8798a9; }

.indicator-grid > div {
  background: linear-gradient(135deg, #f7fbfd, #f1f7fa);
  border-color: #dfeaf0;
}
.indicator-grid span { color: #607b8d; }
.indicator-grid strong { color: #1e3047; }
.indicator-grid small { color: #8999a8; }

.tabs button {
  background: #f5f9fc !important;
  border-color: #dbe7ed !important;
  color: #607d91 !important;
}
.tabs button.active {
  background: #eafafa !important;
  color: #087f70 !important;
}

.risk-key { color: #60798d; }
.event-list > div { border-bottom-color: #edf2f5; }
.event-list time { color: #7890a1; }
.event-list p b { color: #294057; }
.event-list p small { color: #8396a6; }

/* Viewport fit and clipping fixes */
.brand {
  min-width: 520px;
  padding: 0 18px 0 10px;
}
.brand > div:last-child {
  min-width: 0;
  padding-bottom: 2px;
}
.brand h1 {
  line-height: 1.2;
  white-space: nowrap;
}
.brand p {
  line-height: 1.4;
}

.dashboard {
  grid-template-rows: 270px 352px 250px;
}
.panel {
  padding-bottom: 18px;
}
.score-chart .chart,
.vitals > .chart {
  min-height: 0;
  overflow: visible;
}
.indicators {
  padding-bottom: 16px;
}
.indicator-grid {
  gap: 9px;
  margin-top: 12px;
}
.indicator-grid > div {
  min-height: 76px;
  padding: 9px 11px;
}
.indicator-grid strong {
  margin-top: 6px;
  font-size: 13px;
  line-height: 1.25;
}
.indicator-grid small {
  margin-top: 4px;
  font-size: 9px;
  line-height: 1.35;
}

@media (max-width: 1500px) {
  .brand { min-width: 450px; }
  .brand h1 { font-size: 25px; }
}

/* Match the unified light-blue visual language used by the product */
.medical-bi {
  --cyan: #38bdf8;
  --mint: #0ea5e9;
  --text: #202b3d;
  --muted: #778395;
  background:
    radial-gradient(circle at 9% 8%, rgba(56, 189, 248, .12), transparent 26%),
    radial-gradient(circle at 91% 18%, rgba(14, 165, 233, .1), transparent 28%),
    linear-gradient(rgba(56, 189, 248, .035) 1px, transparent 1px),
    linear-gradient(90deg, rgba(56, 189, 248, .035) 1px, transparent 1px),
    linear-gradient(145deg, #f4fbff 0%, #eef9fe 52%, #f8fcff 100%);
  background-size: auto, auto, 34px 34px, 34px 34px, auto;
}

.topbar {
  background: rgba(255, 255, 255, .96);
  border-bottom-color: rgba(56, 189, 248, .24);
  box-shadow: 0 10px 30px rgba(45, 126, 164, .08);
}
.topbar::after {
  background: linear-gradient(90deg, transparent, #38bdf8, #0ea5e9, transparent);
  filter: drop-shadow(0 0 6px rgba(56, 189, 248, .3));
}
.brand h1 {
  background: linear-gradient(90deg, #38bdf8, #0ea5e9);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.brand small { color: #198fbd; }
.brand p { color: #6f91a7; }
.online { color: #58788c; }
.online i { background: #22c58b; box-shadow: 0 0 0 5px rgba(34,197,139,.09), 0 0 12px rgba(34,197,139,.65); }

.clock {
  background: linear-gradient(135deg, #effaff, #e8f7ff);
  border-color: rgba(56, 189, 248, .28);
}
.clock span { color: #58788c; }
.clock b { color: #0ea5e9; }
.actions button {
  border-color: rgba(56, 189, 248, .38) !important;
  color: #0ea5e9 !important;
}
.actions button:hover { background: #e8f7ff !important; color: #0284c7 !important; border-color: #38bdf8 !important; }

.kpis article {
  background: rgba(255,255,255,.96);
  border-color: color-mix(in srgb, var(--tone) 28%, #d2edf9);
  box-shadow: inset 3px 0 var(--tone), 0 9px 22px rgba(45, 126, 164, .075);
}
.kpis article:nth-child(1) { --tone: #38bdf8 !important; }
.kpis article:nth-child(2) { --tone: #22b5ed !important; }
.kpis article:nth-child(3) { --tone: #0ea5e9 !important; }
.kpis article:nth-child(4) { --tone: #60a5fa !important; }
.kpis article:nth-child(5) { --tone: #7dd3fc !important; }
.kpis article:nth-child(6) { --tone: #0284c7 !important; }

.panel {
  border-color: rgba(56, 189, 248, .22) !important;
  box-shadow: 0 10px 28px rgba(45, 126, 164, .07) !important;
}
.panel::before {
  background: linear-gradient(#38bdf8,#38bdf8) left top/20px 2px no-repeat,linear-gradient(#38bdf8,#38bdf8) left top/2px 20px no-repeat,linear-gradient(#0ea5e9,#0ea5e9) right bottom/20px 2px no-repeat,linear-gradient(#0ea5e9,#0ea5e9) right bottom/2px 20px no-repeat;
  filter: drop-shadow(0 0 4px rgba(56,189,248,.3));
}
.panel-title p { color: #3787a8 !important; }
.panel-title p b { color: #0ea5e9; }
.panel-title > span { color: #557b91; background: #effaff; border-color: #d2edf9; }
.tabs button.active { border-color: #38bdf8 !important; color: #0284c7 !important; background: #e8f7ff !important; box-shadow: inset 0 -2px #0ea5e9 !important; }
.score-ring { background: radial-gradient(circle,#fff 48%,transparent 50%),conic-gradient(#38bdf8 calc(var(--score)*1%),#dceff7 0); box-shadow: 0 9px 25px rgba(56,189,248,.13); }
.score-ring::before { border-color: rgba(56,189,248,.25); }
.indicator-grid > div { background: linear-gradient(135deg,#f8fcff,#effaff); border-color:#d2edf9; }
</style>
