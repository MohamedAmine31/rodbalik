import { Injectable } from "@angular/core"
import { HttpClient, HttpParams } from "@angular/common/http"
import { Observable, map } from "rxjs"
import { SignalementList, PaginatedResponse, Signalement } from "../models/signalement.model"
import * as L from "leaflet"

@Injectable({
  providedIn: "root",
})
export class SignalementService {
  private apiUrl = "http://localhost:8000/api"
  private map: L.Map | null = null
  private markers: L.Marker[] = []

  constructor(private http: HttpClient) {}

  getSignalements(filters?: any): Observable<Signalement[]> {
    let params = new HttpParams()
    if (filters) {
      Object.keys(filters).forEach(key => {
        if (filters[key]) params = params.set(key, filters[key])
      })
    }

    const url = `${this.apiUrl}/signalements/with-id/`
    console.log("Fetching from:", url, "with params:", params.keys())

    return this.http.get<Signalement[] | PaginatedResponse<Signalement>>(url, { params }).pipe(
      map(response => {
        console.log("API Response:", response)
        let results: Signalement[]
        if (Array.isArray(response)) {
          results = response
        } else {
          results = response?.results || []
        }
        console.log("Mapped results:", results, "Count:", results.length)
        return results
      })
    )
  }

  validerSignalement(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/signalement/valider/${id}/`, {})
  }

  rejeterSignalement(id: number, rejectionReason?: string): Observable<any> {
    const body: any = {}
    if (rejectionReason) body.rejection_reason = rejectionReason
    return this.http.post(`${this.apiUrl}/signalement/rejeter/${id}/`, body)
  }

  deleteSignalement(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/signalement/supprimer/${id}/`)
  }

  getSignalement(id: number): Observable<Signalement> {
    return this.http.get<Signalement>(`${this.apiUrl}/signalement/${id}/`)
  }

  initializeMap(mapElementId: string, center: [number, number] = [48.8566, 2.3522], zoom: number = 13): L.Map {
    this.map = L.map(mapElementId).setView(center, zoom)

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: "&copy; OpenStreetMap contributors",
      maxZoom: 19,
    }).addTo(this.map)

    return this.map
  }

  addSignalementMarkers(signalements: Signalement[]): void {
    if (!this.map) return

    this.clearMarkers()

    signalements.forEach(signalement => {
      if (signalement.latitude && signalement.longitude) {
        this.addMarker([signalement.latitude, signalement.longitude], signalement)
      }
    })
  }

  addMarker(coords: [number, number], signalement: Signalement): L.Marker {
    if (!this.map) throw new Error("Map not initialized")

    const marker = L.marker(coords).addTo(this.map)
    marker.bindPopup(`
      <div>
        <strong>${signalement.category?.name || "Signalement"}</strong><br>
        ${signalement.description || ""}<br>
        <small>ID: ${signalement.id}</small>
      </div>
    `)

    this.markers.push(marker)
    return marker
  }

  clearMarkers(): void {
    this.markers.forEach(marker => marker.remove())
    this.markers = []
  }

  fitBoundsToMarkers(): void {
    if (!this.map || this.markers.length === 0) return

    const group = new L.FeatureGroup(this.markers)
    this.map.fitBounds(group.getBounds(), { padding: [50, 50] })
  }

  getMap(): L.Map | null {
    return this.map
  }
}
