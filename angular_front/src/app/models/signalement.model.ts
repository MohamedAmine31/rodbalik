export interface SignalementList {
  id: number
  description: string
  category_name: string
  category_color: string
  latitude: number | null
  longitude: number | null
  photo: string | null
  statut: 'EN_COURS' | 'APPROUVE' | 'REJETE'
  author_name: string
  date: string
}

export interface PaginatedResponse<T> {
  count: number
  next: string | null
  previous: string | null
  results: T[]
}

export interface Signalement {
  id: number
  description: string
  photo?: string | null
  latitude?: number | null
  longitude?: number | null
  statut: 'EN_COURS' | 'APPROUVE' | 'REJETE'
  date: string
  category: {
    id: number
    name: string
    description?: string
    color: string
    icon: string
  }
  author: {
    id: number
    username: string
    email: string
    role: 'citizen' | 'admin'
  }
}

export interface Category {
  id: number
  name: string
  description: string
  icon: string // Nom de l'icône (ex: trash, lightbulb, etc.)
  color: string // Couleur hexadécimale (ex: #3B82F6)
  is_active: boolean
  created_at?: string
  signalements_count?: number
}
