import { Component, OnInit } from "@angular/core"
import { SignalementService } from "../../services/signalement.service"
import { AuthService } from "../../services/auth.service"
import { User } from "../../models/user.model"
import { SignalementList } from "../../models/signalement.model"

@Component({
  selector: "app-dashboard",
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.css"],
  standalone: false,
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null
  statistiques: any = null
  isLoading = true

  constructor(
    private signalementService: SignalementService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user
    })

    this.loadStatistiques()
  }

  loadStatistiques(): void {
    this.signalementService.getSignalements().subscribe({
      next: (signalements) => {
        this.calculateStatistiques(signalements)
        this.isLoading = false
      },
      error: (error) => {
        console.error("Erreur lors du chargement des signalements:", error)
        this.isLoading = false
      },
    })
  }

  calculateStatistiques(signalements: any[]): void {
    const total = signalements.length
    const en_cours = signalements.filter(s => s.statut === 'EN_COURS').length

    // Calculate category statistics
    const categories: { [key: string]: number } = {}
    signalements.forEach(s => {
      const cat = s.category?.name || 'autre'
      categories[cat] = (categories[cat] || 0) + 1
    })

    // Get recent signalements (last 5)
    const recents = signalements
      .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
      .slice(0, 5)
      .map(s => ({
        id: s.id,
        titre: s.description,
        statut: s.statut,
        date_creation: s.date
      }))

    this.statistiques = {
      global: {
        total,
        en_cours,
        valides: signalements.filter(s => s.statut === 'APPROUVE').length,
        refuses: signalements.filter(s => s.statut === 'REJETE').length
      },
      par_categorie: categories,
      recents
    }


  }

  getTotalSignalements(): number {
    return this.statistiques?.global?.total || 0
  }

  getValidatedSignalements(): number {
    return this.statistiques?.global?.valides || 0
  }

  getPendingSignalements(): number {
    return this.statistiques?.global?.en_cours || 0
  }

  getRejectedSignalements(): number {
    return this.statistiques?.global?.refuses || 0
  }

  getChangePercentage(current: number, previous: number): string {
    if (previous === 0) return "+100%"
    const change = ((current - previous) / previous) * 100
    return change > 0 ? `+${change.toFixed(1)}%` : `${change.toFixed(1)}%`
  }

  getCategoriesArray(): Array<{ nom: string; count: number }> {
    if (!this.statistiques?.par_categorie) return []
    return Object.keys(this.statistiques.par_categorie).map((key) => ({
      nom: this.formatCategoryName(key),
      count: this.statistiques.par_categorie[key],
    }))
  }

  formatCategoryName(key: string): string {
    const names: { [key: string]: string } = {
      infrastructure: "Infrastructure",
      securite: "Sécurité",
      environnement: "Environnement",
      autre: "Autre",
    }
    return names[key] || key
  }

  getStatutBadgeClass(statut: string): string {
    switch (statut) {
      case "EN_COURS":
        return "badge-warning"
      case "VALIDE":
        return "badge-success"
      case "REFUSE":
        return "badge-danger"
      default:
        return "badge-info"
    }
  }

  getStatutLabel(statut: string): string {
    switch (statut) {
      case "EN_COURS":
        return "En cours"
      case "VALIDE":
        return "Validé"
      case "REFUSE":
        return "Refusé"
      default:
        return statut
    }
  }
}
