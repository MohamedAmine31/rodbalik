import { Component, OnInit } from "@angular/core"
import { UserService } from "../../services/user.service"
import { AuthService } from "../../services/auth.service"
import { User } from "../../models/user.model"

interface UsersResponse {
  count: number
  next: string | null
  previous: string | null
  results: User[]
}

@Component({
  selector: "app-users",
  templateUrl: "./users.component.html",
  styleUrls: ["./users.component.css"],
  standalone: false,
})
export class UsersComponent implements OnInit {
  currentUser: User | null = null
  users: User[] = []
  isLoading = true

  constructor(
    private userService: UserService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user
    })

    this.loadUsers()
  }

  loadUsers(): void {
    this.userService.getUsers().subscribe({
      next: (response) => {
        this.users = response.results
        this.isLoading = false
      },
      error: (error) => {
        console.error("Erreur lors du chargement des utilisateurs:", error)
        this.isLoading = false
      },
    })
  }

  getRoleBadgeClass(role: string): string {
    switch (role.toLowerCase()) {
      case "admin":
        return "badge-danger"
      case "moderator":
        return "badge-warning"
      case "user":
        return "badge-info"
      default:
        return "badge-secondary"
    }
  }

  getRoleLabel(role: string): string {
    switch (role.toLowerCase()) {
      case "admin":
        return "Administrateur"
      case "moderator":
        return "Modérateur"
      case "user":
        return "Utilisateur"
      default:
        return "Inconnu"
    }
  }

  getUserInitials(user: User): string {
    const firstInitial = user.first_name ? user.first_name.charAt(0).toUpperCase() : "";
    const lastInitial = user.last_name ? user.last_name.charAt(0).toUpperCase() : "";
    return firstInitial + lastInitial;
  }
}
