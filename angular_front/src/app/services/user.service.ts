import { Injectable } from "@angular/core"
import { HttpClient, HttpParams } from "@angular/common/http"
import { Observable } from "rxjs"
import { User } from "../models/user.model"

@Injectable({
  providedIn: "root",
})
export class UserService {
  private apiUrl = "http://localhost:8000/api"

  constructor(private http: HttpClient) {}

  // Note: Django API doesn't have a dedicated users endpoint in the provided documentation
  // This service is prepared for future implementation
  getUsers(filters?: any): Observable<{ count: number; next: string | null; previous: string | null; results: User[] }> {
    let params = new HttpParams()
    if (filters) {
      Object.keys(filters).forEach((key) => {
        if (filters[key]) {
          params = params.set(key, filters[key])
        }
      })
    }
    return this.http.get<{ count: number; next: string | null; previous: string | null; results: User[] }>(`${this.apiUrl}/users/`, { params })
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/users/${id}/`)
  }

  updateUser(id: number, user: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/users/${id}/`, user)
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${id}/`)
  }

  toggleUserStatus(id: number): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/users/${id}/toggle-status/`, {})
  }
}
