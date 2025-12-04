import { Component, OnInit } from "@angular/core"
import { AuthService } from "../../services/auth.service"
import { User } from "../../models/user.model"

@Component({
  selector: "app-parametres",
  templateUrl: "./parametres.component.html",
  styleUrls: ["./parametres.component.css"],
  standalone: false,
})
export class ParametresComponent implements OnInit {
  currentUser: User | null = null
  isLoading = true

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.loadProfile()
  }

  loadProfile(): void {
    this.authService.getProfile().subscribe({
      next: (profile) => {
        this.currentUser = profile
        this.isLoading = false
      },
      error: (error) => {
        console.error("Erreur lors du chargement du profil:", error)
        // Fallback sur l'utilisateur en cache
        this.currentUser = this.authService.getCurrentUser()
        this.isLoading = false
      },
    })
  }

  getInitials(): string {
    if (!this.currentUser) return "AD"
    return `${this.currentUser.first_name?.charAt(0) || ""}${this.currentUser.last_name?.charAt(0) || ""}`.toUpperCase()
  }

  getRoleLabel(role: string): string {
    switch (role) {
      case "ADMIN":
        return "Administrateur"
      case "CITOYEN":
        return "Citoyen"
      default:
        return role
    }
  }
}
