/**
 * Three.js 场景管理器
 * 用于创建和管理3D交互场景
 */
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader'
import { EffectComposer } from 'three/examples/jsm/postprocessing/EffectComposer'
import { RenderPass } from 'three/examples/jsm/postprocessing/RenderPass'
import { OutlinePass } from 'three/examples/jsm/postprocessing/OutlinePass'

export interface SceneOptions {
  background?: string | number
  enablePostProcessing?: boolean
  enableShadows?: boolean
}

export class ThreeSceneManager {
  public container: HTMLElement
  public scene: THREE.Scene
  public camera: THREE.PerspectiveCamera
  public renderer: THREE.WebGLRenderer
  public controls: OrbitControls
  public composer: EffectComposer | null = null
  public outlinePass: OutlinePass | null = null
  public models: Map<string, THREE.Object3D> = new Map()
  
  private raycaster: THREE.Raycaster
  private mouse: THREE.Vector2
  private animationId: number | null = null
  private clock: THREE.Clock
  private mixers: THREE.AnimationMixer[] = []

  // 回调
  public onObjectClick?: (name: string, object: THREE.Object3D) => void
  public onObjectHover?: (name: string | null) => void

  constructor(container: HTMLElement, options: SceneOptions = {}) {
    this.container = container
    this.raycaster = new THREE.Raycaster()
    this.mouse = new THREE.Vector2()
    this.clock = new THREE.Clock()

    this.initScene(options)
    this.initCamera()
    this.initRenderer(options)
    this.initControls()
    this.initLights()
    
    if (options.enablePostProcessing !== false) {
      this.initPostProcessing()
    }
    
    this.initEventListeners()
    this.animate()
  }

  private initScene(options: SceneOptions) {
    this.scene = new THREE.Scene()
    
    if (typeof options.background === 'string') {
      // 渐变背景
      const canvas = document.createElement('canvas')
      canvas.width = 2
      canvas.height = 512
      const ctx = canvas.getContext('2d')!
      const gradient = ctx.createLinearGradient(0, 0, 0, 512)
      gradient.addColorStop(0, '#1a1a2e')
      gradient.addColorStop(1, '#16213e')
      ctx.fillStyle = gradient
      ctx.fillRect(0, 0, 2, 512)
      this.scene.background = new THREE.CanvasTexture(canvas)
    } else {
      this.scene.background = new THREE.Color(options.background ?? 0x1a1a2e)
    }
  }


  private initCamera() {
    const { width, height } = this.container.getBoundingClientRect()
    this.camera = new THREE.PerspectiveCamera(50, width / height, 0.1, 1000)
    this.camera.position.set(0, 2, 8)
  }

  private initRenderer(options: SceneOptions) {
    this.renderer = new THREE.WebGLRenderer({ 
      antialias: true,
      alpha: true 
    })
    this.renderer.setSize(this.container.clientWidth, this.container.clientHeight)
    this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
    
    if (options.enableShadows !== false) {
      this.renderer.shadowMap.enabled = true
      this.renderer.shadowMap.type = THREE.PCFSoftShadowMap
    }
    
    this.renderer.outputColorSpace = THREE.SRGBColorSpace
    this.renderer.toneMapping = THREE.ACESFilmicToneMapping
    this.renderer.toneMappingExposure = 1.2
    this.container.appendChild(this.renderer.domElement)
  }

  private initControls() {
    this.controls = new OrbitControls(this.camera, this.renderer.domElement)
    this.controls.enableDamping = true
    this.controls.dampingFactor = 0.05
    this.controls.minDistance = 2
    this.controls.maxDistance = 50
    this.controls.maxPolarAngle = Math.PI * 0.9
  }

  private initLights() {
    const ambientLight = new THREE.AmbientLight(0xffffff, 0.5)
    this.scene.add(ambientLight)

    const mainLight = new THREE.DirectionalLight(0xffffff, 1)
    mainLight.position.set(5, 10, 7)
    mainLight.castShadow = true
    mainLight.shadow.mapSize.width = 2048
    mainLight.shadow.mapSize.height = 2048
    this.scene.add(mainLight)

    const fillLight = new THREE.DirectionalLight(0x8888ff, 0.3)
    fillLight.position.set(-5, 5, -5)
    this.scene.add(fillLight)
  }

  private initPostProcessing() {
    this.composer = new EffectComposer(this.renderer)
    const renderPass = new RenderPass(this.scene, this.camera)
    this.composer.addPass(renderPass)

    this.outlinePass = new OutlinePass(
      new THREE.Vector2(this.container.clientWidth, this.container.clientHeight),
      this.scene,
      this.camera
    )
    this.outlinePass.edgeStrength = 3
    this.outlinePass.edgeGlow = 1
    this.outlinePass.visibleEdgeColor.set('#00ffff')
    this.composer.addPass(this.outlinePass)
  }

  private initEventListeners() {
    window.addEventListener('resize', this.onResize.bind(this))
    this.renderer.domElement.addEventListener('click', this.onClick.bind(this))
    this.renderer.domElement.addEventListener('mousemove', this.onMouseMove.bind(this))
  }

  private onResize() {
    const { width, height } = this.container.getBoundingClientRect()
    this.camera.aspect = width / height
    this.camera.updateProjectionMatrix()
    this.renderer.setSize(width, height)
    this.composer?.setSize(width, height)
  }

  private onClick(event: MouseEvent) {
    this.updateMouse(event)
    this.raycaster.setFromCamera(this.mouse, this.camera)
    const intersects = this.raycaster.intersectObjects(Array.from(this.models.values()), true)

    if (intersects.length > 0) {
      const clicked = this.findParentModel(intersects[0].object)
      if (clicked) {
        const name = this.getModelName(clicked)
        if (name && this.onObjectClick) {
          this.onObjectClick(name, clicked)
        }
      }
    }
  }

  private onMouseMove(event: MouseEvent) {
    this.updateMouse(event)
    this.raycaster.setFromCamera(this.mouse, this.camera)
    const intersects = this.raycaster.intersectObjects(Array.from(this.models.values()), true)

    if (intersects.length > 0) {
      const hovered = this.findParentModel(intersects[0].object)
      if (hovered && this.outlinePass) {
        this.outlinePass.selectedObjects = [hovered]
        this.renderer.domElement.style.cursor = 'pointer'
        const name = this.getModelName(hovered)
        this.onObjectHover?.(name)
      }
    } else {
      if (this.outlinePass) this.outlinePass.selectedObjects = []
      this.renderer.domElement.style.cursor = 'default'
      this.onObjectHover?.(null)
    }
  }

  private updateMouse(event: MouseEvent) {
    const rect = this.renderer.domElement.getBoundingClientRect()
    this.mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1
    this.mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1
  }

  private findParentModel(object: THREE.Object3D): THREE.Object3D | null {
    let current: THREE.Object3D | null = object
    while (current) {
      if (this.models.has(current.name)) return current
      current = current.parent
    }
    return null
  }

  private getModelName(object: THREE.Object3D): string | null {
    for (const [name, model] of this.models) {
      if (model === object) return name
    }
    return null
  }

  private animate() {
    this.animationId = requestAnimationFrame(this.animate.bind(this))
    const delta = this.clock.getDelta()
    
    this.mixers.forEach(mixer => mixer.update(delta))
    this.controls.update()
    
    if (this.composer) {
      this.composer.render()
    } else {
      this.renderer.render(this.scene, this.camera)
    }
  }


  // ========== 公共方法 ==========

  /** 加载GLTF模型 */
  async loadModel(name: string, url: string, options?: {
    position?: THREE.Vector3
    scale?: number
    rotation?: THREE.Euler
  }): Promise<THREE.Object3D> {
    const loader = new GLTFLoader()
    
    return new Promise((resolve, reject) => {
      loader.load(url, (gltf) => {
        const model = gltf.scene
        model.name = name
        
        if (options?.position) model.position.copy(options.position)
        if (options?.scale) model.scale.setScalar(options.scale)
        if (options?.rotation) model.rotation.copy(options.rotation)

        model.traverse((child) => {
          if (child instanceof THREE.Mesh) {
            child.castShadow = true
            child.receiveShadow = true
          }
        })

        // 处理动画
        if (gltf.animations.length > 0) {
          const mixer = new THREE.AnimationMixer(model)
          gltf.animations.forEach(clip => {
            mixer.clipAction(clip).play()
          })
          this.mixers.push(mixer)
        }

        this.scene.add(model)
        this.models.set(name, model)
        resolve(model)
      }, undefined, reject)
    })
  }

  /** 添加3D对象 */
  addObject(name: string, object: THREE.Object3D) {
    object.name = name
    this.scene.add(object)
    this.models.set(name, object)
  }

  /** 移除对象 */
  removeObject(name: string) {
    const obj = this.models.get(name)
    if (obj) {
      this.scene.remove(obj)
      this.models.delete(name)
    }
  }

  /** 聚焦到对象 */
  focusOn(target: THREE.Object3D | string, duration: number = 1000) {
    const obj = typeof target === 'string' ? this.models.get(target) : target
    if (!obj) return

    const box = new THREE.Box3().setFromObject(obj)
    const center = box.getCenter(new THREE.Vector3())
    const size = box.getSize(new THREE.Vector3())
    const maxDim = Math.max(size.x, size.y, size.z)
    const distance = maxDim * 2.5

    const targetPos = {
      x: center.x + distance * 0.5,
      y: center.y + distance * 0.3,
      z: center.z + distance
    }

    this.animateCamera(targetPos, center, duration)
  }

  /** 重置视角 */
  resetView(duration: number = 1000) {
    this.animateCamera({ x: 0, y: 2, z: 8 }, { x: 0, y: 0, z: 0 }, duration)
  }

  /** 相机动画 */
  private animateCamera(
    position: { x: number; y: number; z: number },
    target: { x: number; y: number; z: number },
    duration: number
  ) {
    const startPos = this.camera.position.clone()
    const startTarget = this.controls.target.clone()
    const startTime = performance.now()

    const animate = () => {
      const elapsed = performance.now() - startTime
      const t = Math.min(elapsed / duration, 1)
      const eased = 1 - Math.pow(1 - t, 3) // easeOutCubic

      this.camera.position.lerpVectors(
        startPos,
        new THREE.Vector3(position.x, position.y, position.z),
        eased
      )
      this.controls.target.lerpVectors(
        startTarget,
        new THREE.Vector3(target.x, target.y, target.z),
        eased
      )

      if (t < 1) requestAnimationFrame(animate)
    }
    animate()
  }

  /** 高亮对象 */
  highlight(name: string, enabled: boolean) {
    const obj = this.models.get(name)
    if (obj && this.outlinePass) {
      this.outlinePass.selectedObjects = enabled ? [obj] : []
    }
  }

  /** 设置对象透明度 */
  setOpacity(name: string, opacity: number, duration: number = 500) {
    const obj = this.models.get(name)
    if (!obj) return

    obj.traverse((child) => {
      if (child instanceof THREE.Mesh && child.material) {
        const mat = child.material as THREE.MeshStandardMaterial
        mat.transparent = true
        
        const startOpacity = mat.opacity
        const startTime = performance.now()
        
        const animate = () => {
          const t = Math.min((performance.now() - startTime) / duration, 1)
          mat.opacity = startOpacity + (opacity - startOpacity) * t
          if (t < 1) requestAnimationFrame(animate)
        }
        animate()
      }
    })
  }

  /** 切换自动旋转 */
  setAutoRotate(enabled: boolean, speed: number = 0.5) {
    this.controls.autoRotate = enabled
    this.controls.autoRotateSpeed = speed
  }

  /** 截图 */
  captureScreenshot(): string {
    this.renderer.render(this.scene, this.camera)
    return this.renderer.domElement.toDataURL('image/png')
  }

  /** 销毁 */
  dispose() {
    if (this.animationId) cancelAnimationFrame(this.animationId)
    window.removeEventListener('resize', this.onResize.bind(this))
    
    this.models.forEach(model => this.scene.remove(model))
    this.models.clear()
    this.mixers = []
    
    this.renderer.dispose()
    this.container.removeChild(this.renderer.domElement)
  }
}
