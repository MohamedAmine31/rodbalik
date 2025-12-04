import { Injectable } from "@angular/core"
import { HttpClient } from "@angular/common/http"
import { BehaviorSubject, Observable, tap, catchError, throwError } from "rxjs"
import { User } from "../models/user.model"

interface TokenResponse {
  access: string
  refresh: string
}

interface TokenRefreshResponse {
  access: string
}

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private apiUrl = "http://localhost:8000/api"
  private currentUserSubject = new BehaviorSubject<User | null>(null)
  public currentUser$ = this.currentUserSubject.asObservable()

  private failedAttempts = 0
  private lockoutUntil: number | null = null
  private readonly MAX_ATTEMPTS = 3
  private readonly LOCKOUT_DURATION = 10000 // 10 seconds

  constructor(private http: HttpClient) {
    // Charger l'utilisateur depuis le localStorage au démarrage
    const storedUser = localStorage.getItem("currentUser")
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser))
    }

    // Load failed attempts and lockout from localStorage
    const storedFailedAttempts = localStorage.getItem("failedAttempts")
    if (storedFailedAttempts) {
      this.failedAttempts = parseInt(storedFailedAttempts, 10)
    }
    const storedLockoutUntil = localStorage.getItem("lockoutUntil")
    if (storedLockoutUntil) {
      this.lockoutUntil = parseInt(storedLockoutUntil, 10)
    }
  }

  public isLockedOut(): boolean {
    if (this.lockoutUntil && Date.now() < this.lockoutUntil) {
      return true
    }
    if (this.lockoutUntil && Date.now() >= this.lockoutUntil) {
      this.resetLockout()
    }
    return false
  }

  public getLockoutTimeRemaining(): number {
    if (this.lockoutUntil) {
      return Math.max(0, this.lockoutUntil - Date.now())
    }
    return 0
  }

  private resetLockout(): void {
    this.failedAttempts = 0
    this.lockoutUntil = null
    localStorage.removeItem("failedAttempts")
    localStorage.removeItem("lockoutUntil")
  }

  private incrementFailedAttempts(): void {
    this.failedAttempts++
    localStorage.setItem("failedAttempts", this.failedAttempts.toString())
    if (this.failedAttempts >= this.MAX_ATTEMPTS) {
      this.lockoutUntil = Date.now() + this.LOCKOUT_DURATION
      localStorage.setItem("lockoutUntil", this.lockoutUntil.toString())
    }
  }

  login(username: string, password: string): Observable<TokenResponse> {
    if (this.isLockedOut()) {
      return throwError(() => new Error("Too many failed attempts. Please wait before trying again."))
    }

    return this.http
      .post<TokenResponse>(`${this.apiUrl}/token/`, {
        username,
        password,
      })
      .pipe(
        tap((response) => {
          if (response.access && response.refresh) {
            // Reset failed attempts on successful login
            this.resetLockout()
            // Stocker les tokens JWT
            localStorage.setItem("access_token", response.access)
            localStorage.setItem("refresh_token", response.refresh)

            // Récupérer le profil utilisateur après connexion
            this.getProfile().subscribe({
              next: (profile) => {
                localStorage.setItem("currentUser", JSON.stringify(profile))
                this.currentUserSubject.next(profile)
              },
              error: (error) => {
                console.error("Erreur lors de la récupération du profil:", error)
              },
            })
          }
        }),
        catchError((error) => {
          this.incrementFailedAttempts()
          console.error("Erreur de connexion:", error)
          return throwError(() => error)
        }),
      )
  }

  refreshToken(): Observable<TokenRefreshResponse> {
    const refreshToken = localStorage.getItem("refresh_token")

    if (!refreshToken) {
      return throwError(() => new Error("No refresh token available"))
    }

    return this.http
      .post<TokenRefreshResponse>(`${this.apiUrl}/token/refresh/`, {
        refresh: refreshToken,
      })
      .pipe(
        tap((response) => {
          if (response.access) {
            localStorage.setItem("access_token", response.access)
          }
        }),
        catchError((error) => {
          // Si le refresh échoue, déconnecter l'utilisateur
          this.logout()
          return throwError(() => error)
        }),
      )
  }

  logout(): void {
    localStorage.removeItem("access_token")
    localStorage.removeItem("refresh_token")
    localStorage.removeItem("currentUser")
    this.currentUserSubject.next(null)
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem("access_token")
  }

  getAccessToken(): string | null {
    return localStorage.getItem("access_token")
  }

  getRefreshToken(): string | null {
    return localStorage.getItem("refresh_token")
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value
  }

  getProfile(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/users/me/`)
  }

  updateProfile(profile: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/users/me/`, profile).pipe(
      tap((updatedProfile) => {
        localStorage.setItem("currentUser", JSON.stringify(updatedProfile))
        this.currentUserSubject.next(updatedProfile)
      }),
    )
  }
}
