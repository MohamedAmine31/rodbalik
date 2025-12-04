import { Component, OnInit } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import { SignalementService } from "../../services/signalement.service"
import { SignalementList, Signalement } from "../../models/signalement.model"
import { SignalementsMapComponent } from "../map/signalements-map.component"

@Component({
  selector: "app-signalements",
  templateUrl: "./signalements.component.html",
  styleUrls: ["./signalements.component.css"],
  standalone: true,
  imports: [CommonModule, FormsModule, SignalementsMapComponent],
})
export class SignalementsComponent implements OnInit {
  signalements: SignalementList[] = []
  isLoading = true
  totalCount = 0
  filtreStatut = ""
  availableStatuses: string[] = ['EN_COURS', 'APPROUVE', 'REJETE']
  selectedSignalement: SignalementList | null = null
  showValidationModal = false
  validationAction: "valider" | "rejeter" = "valider"
  moderation_comment = ""
  errorMessage = ""
  viewMode: string = "table"
  showDetailsModal = false
  selectedSignalementDetails: Signalement | null = null
  isLoadingDetails = false
  private apiBaseUrl = "http://localhost:8000"

  constructor(private signalementService: SignalementService) {}

  ngOnInit(): void {
    this.loadSignalements()
  }

  getFilters(): any {
    const filters: any = {}
    if (this.filtreStatut) filters.statut = this.filtreStatut
    return filters
  }

  loadSignalements(): void {
    console.log('Loading signalements...')
    this.isLoading = true
    this.errorMessage = ""
    const filters = this.getFilters()
    console.log('Filters:', filters)
    this.signalementService.getSignalements(filters).subscribe({
      next: (signalements: Signalement[]) => {
        console.log('Received signalements:', signalements)
        this.signalements = signalements.map(s => ({
          id: s.id,
          description: s.description,
          category_name: (s as any).category_name || s.category?.name || '',
          category_color: (s as any).category_color || s.category?.color || '#3B82F6',
          latitude: s.latitude ?? null,
          longitude: s.longitude ?? null,
          photo: s.photo || null,
          statut: s.statut,
          author_name: (s as any).author_name || s.author?.username || '',
          date: s.date,
        }))
        console.log('Mapped signalements:', this.signalements)
        this.totalCount = this.signalements.length
        this.isLoading = false
        console.log('Total count:', this.totalCount)
      },
      error: (err) => {
        console.error('Error loading signalements:', err)
        this.signalements = []
        this.isLoading = false
        this.errorMessage = "Erreur lors du chargement des signalements."
      }
    })
  }

  onFilterChange(): void {
    this.loadSignalements()
  }

  openValidationModal(signalement: SignalementList, action: "valider" | "rejeter") {
    this.selectedSignalement = signalement
    this.validationAction = action
    this.showValidationModal = true
    this.moderation_comment = ""
  }

  closeValidationModal() {
    this.showValidationModal = false
    this.selectedSignalement = null
    this.moderation_comment = ""
  }

  confirmValidation() {
    if (!this.selectedSignalement) return
    if (this.validationAction === "rejeter" && !this.moderation_comment) {
      alert("Veuillez fournir un commentaire de modération")
      return
    }

    const serviceCall = this.validationAction === "valider"
      ? this.signalementService.validerSignalement(this.selectedSignalement.id)
      : this.signalementService.rejeterSignalement(this.selectedSignalement.id, this.moderation_comment)

    serviceCall.subscribe({
      next: () => {
        this.closeValidationModal()
        this.loadSignalements()
      },
      error: () => alert("Erreur lors de la validation"),
    })
  }

  deleteSignalement(id: number) {
    if (confirm("Voulez-vous vraiment supprimer ce signalement ?")) {
      this.signalementService.deleteSignalement(id).subscribe({
        next: () => this.loadSignalements(),
        error: (err) => console.error('Delete error:', err),
      })
    }
  }

  openDetailsModal(id: number) {
    this.isLoadingDetails = true
    this.showDetailsModal = true
    this.signalementService.getSignalement(id).subscribe({
      next: (signalement: Signalement) => {
        console.log('Signalement details:', signalement)
        console.log('Photo value:', signalement.photo)
        this.selectedSignalementDetails = signalement
        this.isLoadingDetails = false
      },
      error: (err) => {
        console.error('Error loading signalement details:', err)
        this.isLoadingDetails = false
        alert('Erreur lors du chargement des détails')
      }
    })
  }

  closeDetailsModal() {
    this.showDetailsModal = false
    this.selectedSignalementDetails = null
    this.isLoadingDetails = false
  }

  getStatutBadgeClass(statut: string) {
    switch (statut) {
      case "EN_COURS": return "badge-warning"
      case "APPROUVE": return "badge-success"
      case "REJETE": return "badge-danger"
      default: return "badge-info"
    }
  }

  getStatutLabel(statut: string) {
    switch (statut) {
      case "EN_COURS": return "En cours"
      case "APPROUVE": return "Approuvé"
      case "REJETE": return "Rejeté"
      default: return statut
    }
  }

  getPhotoUrl(photo: string | null | undefined): string | null {
    if (!photo) return null
    if (photo.startsWith('http://') || photo.startsWith('https://')) {
      return photo
    }
    // Ensure photo starts with /
    if (!photo.startsWith('/')) {
      photo = '/' + photo
    }
    // If it's a relative URL, prefix with API base URL
    return `${this.apiBaseUrl}${photo}`
  }

  onImageError(event: Event) {
    const img = event.target as HTMLImageElement
    img.style.display = 'none'
    console.error('Failed to load image:', img.src)
  }
}
