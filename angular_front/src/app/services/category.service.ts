import { Injectable } from "@angular/core"
import { HttpClient } from "@angular/common/http"
import { Observable } from "rxjs"
import { Category, PaginatedResponse } from "../models/signalement.model"

@Injectable({
  providedIn: "root",
})
export class CategoryService {
  private apiUrl = "http://localhost:8000/api"

  constructor(private http: HttpClient) {}

  getCategories(): Observable<PaginatedResponse<Category>> {
    return this.http.get<PaginatedResponse<Category>>(`${this.apiUrl}/categories/`)
  }

  getCategory(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.apiUrl}/categories/${id}/`)
  }

  createCategory(category: Omit<Category, "id" | "created_at" | "signalements_count" | "is_active">): Observable<Category> {
    return this.http.post<Category>(`${this.apiUrl}/categories/`, category)
  }

  updateCategory(id: number, category: Partial<Category>): Observable<Category> {
    return this.http.put<Category>(`${this.apiUrl}/categories/${id}/`, category)
  }

  partialUpdateCategory(id: number, category: Partial<Category>): Observable<Category> {
    return this.http.patch<Category>(`${this.apiUrl}/categories/${id}/`, category)
  }

  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/categories/${id}/`)
  }
}
