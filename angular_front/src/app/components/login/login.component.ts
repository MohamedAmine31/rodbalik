import { Component, OnInit, OnDestroy } from "@angular/core";
import { Router } from "@angular/router";
import { AuthService } from "../../services/auth.service";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faUser, faLock, faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.css"],
  standalone: true,
  imports: [FormsModule, CommonModule, FontAwesomeModule],
})
export class LoginComponent implements OnInit, OnDestroy {
  faUser = faUser;
  faLock = faLock;
  faEye = faEye;
  faEyeSlash = faEyeSlash;

  username = "";
  password = "";
  errorMessage = "";
  isLoading = false;

  showPassword = false;

  // Lockout
  isLockedOut = false;
  lockoutTimeRemaining = 0;
  private countdownInterval: any;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.checkLockoutStatus();
  }

  ngOnDestroy(): void {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
  }

  private checkLockoutStatus(): void {
    // Test : bloque automatiquement 10s si pas déjà bloqué
    if (this.isLockedOut) {
      this.startCountdown();
    }
  }

  private startCountdown(): void {
    this.lockoutTimeRemaining = 10; // 10 secondes
    this.isLockedOut = true;

    this.countdownInterval = setInterval(() => {
      this.lockoutTimeRemaining--;

      if (this.lockoutTimeRemaining <= 0) {
        this.isLockedOut = false;
        clearInterval(this.countdownInterval);
      }
    }, 1000);
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (!this.username || !this.password) {
      this.errorMessage = "Veuillez remplir tous les champs";
      return;
    }

    if (this.isLockedOut) return; // Bloqué pendant le countdown

    this.isLoading = true;
    this.errorMessage = "";

    this.authService.login(this.username, this.password).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(["/dashboard"]);
      },
      error: () => {
        this.isLoading = false;
this.errorMessage = "Identifiants incorrects";

        // Déclenche le blocage de 10 secondes après échec
        this.startCountdown();
      },
    });
  }
}
