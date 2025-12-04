import { Component, AfterViewInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';
import { SignalementList } from '../../models/signalement.model';

@Component({
  selector: 'app-signalements-map',
  template: `<div id="map" [style.height]="height"></div>`,
  styleUrls: ['./signalements-map.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class SignalementsMapComponent implements AfterViewInit, OnChanges {
  @Input() signalements: SignalementList[] = [];
  @Input() height: string = '400px';

  private map: L.Map | undefined;
  private markers: L.Marker[] = [];

  ngAfterViewInit(): void {
    // Fix default marker icons path
    delete (L.Icon.Default.prototype as any)._getIconUrl;
    L.Icon.Default.mergeOptions({
      iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png',
      iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
      shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
    });

    this.map = L.map('map').setView([48.8566, 2.3522], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    this.updateMarkers();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['signalements'] && this.map) {
      this.updateMarkers();
    }
  }

  private updateMarkers(): void {
    if (!this.map) return;

    // Clear existing markers
    this.markers.forEach(marker => this.map!.removeLayer(marker));
    this.markers = [];

    // Add markers for signalements with coordinates
    const validSignalements = this.signalements.filter(s => s.latitude !== null && s.longitude !== null);

    if (validSignalements.length > 0) {
      validSignalements.forEach(signalement => {
        const marker = L.marker([signalement.latitude!, signalement.longitude!])
          .addTo(this.map!)
          .bindPopup(`
            <strong>${signalement.description}</strong><br>
            Catégorie: ${signalement.category_name}<br>
            Statut: ${this.getStatusLabel(signalement.statut)}<br>
            Auteur: ${signalement.author_name}
          `);
        this.markers.push(marker);
      });

      // Fit map to show all markers
      const latlngs = this.markers.map(marker => marker.getLatLng());
      const bounds = L.latLngBounds(latlngs);
      this.map.fitBounds(bounds.pad(0.1));
    }
  }

  private getStatusLabel(statut: string): string {
    switch (statut) {
      case 'EN_COURS': return 'En cours';
      case 'APPROUVE': return 'Approuvé';
      case 'REJETE': return 'Rejeté';
      default: return statut;
    }
  }
}
