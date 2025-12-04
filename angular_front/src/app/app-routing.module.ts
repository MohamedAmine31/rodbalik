import { NgModule } from "@angular/core"
import { RouterModule, type Routes } from "@angular/router"
import { LoginComponent } from "./components/login/login.component"
import { DashboardComponent } from "./components/dashboard/dashboard.component"
import { SignalementsComponent } from "./components/signalements/signalements.component"
import { ParametresComponent } from "./components/parametres/parametres.component"
import { CategoriesComponent } from "./components/categories/categories.component"
import { UsersComponent } from "./components/users/users.component"
import { AuthGuard } from "./guards/auth.guard"
import { AdminGuard } from "./guards/admin.guard"

const routes: Routes = [
  { path: "", component: LoginComponent },
  { path: "login", redirectTo: "", pathMatch: "full" },
  { path: "dashboard", component: DashboardComponent, canActivate: [AuthGuard] },
  { path: "signalements", component: SignalementsComponent, canActivate: [AuthGuard] },
  { path: "categories", component: CategoriesComponent, canActivate: [AuthGuard] },
  { path: "parametres", component: ParametresComponent, canActivate: [AuthGuard] },
  { path: "users", component: UsersComponent, canActivate: [AdminGuard] },
  { path: "**", redirectTo: "" },
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
