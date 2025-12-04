import { Component, OnInit } from "@angular/core"
import { Router, NavigationEnd } from "@angular/router"
import { AuthService } from "./services/auth.service"
import { User } from "./models/user.model"

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
  standalone: false,
})
export class AppComponent implements OnInit {
  title = "rad-balak-admin"
  currentUser: User | null = null
  showLayout = false

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user
    })

    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        const url = event.urlAfterRedirects
        this.showLayout = !(url === "/" || url.includes("/login"))
      }
    })
  }

  logout(): void {
    this.authService.logout()
    this.router.navigate(["/login"])
  }

  getInitials(): string {
    if (!this.currentUser) return "AD"
    const { first_name, last_name } = this.currentUser
    return `${first_name?.charAt(0) || ""}${last_name?.charAt(0) || ""}`.toUpperCase()
  }

  isActiveRoute(route: string): boolean {
    return this.router.url.startsWith(route)
  }
}
