import { NgModule } from "@angular/core"
import { BrowserModule } from "@angular/platform-browser"
import { HttpClientModule, HTTP_INTERCEPTORS } from "@angular/common/http"
import { FormsModule } from "@angular/forms"
import { RouterModule } from "@angular/router"
import { CommonModule } from "@angular/common"

import { AppRoutingModule } from "./app-routing.module"
import { AppComponent } from "./app.component"
import { DashboardComponent } from "./components/dashboard/dashboard.component"
import { SignalementsComponent } from "./components/signalements/signalements.component"
import { ParametresComponent } from "./components/parametres/parametres.component"
import { LoginComponent } from "./components/login/login.component"

import { AuthService } from "./services/auth.service"
import { SignalementService } from "./services/signalement.service"
import { UserService } from "./services/user.service"
import { AuthGuard } from "./guards/auth.guard"
import { AuthInterceptor } from "./interceptors/auth.interceptor"
import { NgxBootstrapIconsModule, allIcons } from 'ngx-bootstrap-icons';
import { CategoriesComponent } from './components/categories/categories.component';
import { UsersComponent } from './components/users/users.component'

@NgModule({
  declarations: [AppComponent, DashboardComponent, ParametresComponent, CategoriesComponent, UsersComponent],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule, FormsModule, RouterModule, CommonModule, NgxBootstrapIconsModule.pick(allIcons), SignalementsComponent],
  providers: [
    AuthService,
    SignalementService,
    UserService,
    AuthGuard,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
