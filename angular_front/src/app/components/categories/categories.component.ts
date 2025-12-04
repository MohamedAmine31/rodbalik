import { Component, OnInit } from '@angular/core';
import { CategoryService } from '../../services/category.service';
import { Category } from '../../models/signalement.model';

@Component({
  selector: 'app-categories',
  standalone: false,
  templateUrl: './categories.component.html',
  styleUrl: './categories.component.css'
})
export class CategoriesComponent implements OnInit {
  categories: Category[] = [];
  isLoading = true;
  showCreateModal = false;
  showEditModal = false;
  editingCategory: Category | null = null;

  newCategory: Partial<Category> = {
    name: '',
    description: '',
    icon: '',
    color: '#3B82F6'
  };

  bootstrapIcons: string[] = [
    'bi bi-tag-fill', 'bi bi-exclamation-triangle-fill', 'bi bi-tools', 'bi bi-lightbulb-fill',
    'bi bi-trash-fill', 'bi bi-car-front-fill', 'bi bi-house-fill', 'bi bi-building',
    'bi bi-signpost-split-fill', 'bi bi-tree-fill', 'bi bi-droplet-fill', 'bi bi-fire',
    'bi bi-shield-fill', 'bi bi-people-fill', 'bi bi-wrench', 'bi bi-gear-fill',
    'bi bi-bell-fill', 'bi bi-flag-fill', 'bi bi-geo-alt-fill', 'bi bi-camera-fill',
    'bi bi-telephone-fill', 'bi bi-envelope-fill', 'bi bi-globe', 'bi bi-star-fill',
    'bi bi-heart-fill', 'bi bi-hand-thumbs-up-fill', 'bi bi-chat-dots-fill', 'bi bi-clock-fill',
    'bi bi-calendar-event-fill', 'bi bi-briefcase-fill', 'bi bi-cup-hot-fill', 'bi bi-music-note'
  ];

  constructor(private categoryService: CategoryService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (response) => {
        console.log('Categories response:', response);
        this.categories = response.results || [];
        console.log('Loaded categories:', this.categories);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des catégories:', error);
        this.isLoading = false;
      }
    });
  }

  openCreateModal(): void {
    this.newCategory = {
      name: '',
      description: '',
      icon: '',
      color: '#3B82F6'
    };
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
  }

  editCategory(category: Category): void {
    if (!category.id) {
      console.error('Category ID is missing', category);
      alert('Erreur: Impossible de modifier cette catégorie (ID manquant)');
      return;
    }
    this.categoryService.getCategory(category.id).subscribe({
      next: (fullCategory) => {
        this.editingCategory = { ...fullCategory };
        this.showEditModal = true;
      },
      error: (error) => {
        console.error('Erreur lors du chargement de la catégorie:', error);
        alert('Erreur lors du chargement de la catégorie');
      }
    });
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.editingCategory = null;
  }

  selectIcon(icon: string): void {
    this.newCategory.icon = icon;
  }

  get currentIcons(): string[] {
    return this.bootstrapIcons;
  }

  createCategory(): void {
    if (!this.newCategory.name?.trim() || !this.newCategory.description?.trim()) {
      alert('Veuillez remplir tous les champs obligatoires (nom et description)');
      return;
    }

    if (!this.newCategory.color || !/^#[0-9A-F]{6}$/i.test(this.newCategory.color)) {
      alert('Veuillez sélectionner une couleur valide');
      return;
    }

    const categoryData = {
      name: this.newCategory.name.trim(),
      description: this.newCategory.description.trim(),
      icon: this.newCategory.icon || 'bi-tag-fill',
      color: this.newCategory.color
    };

    console.log('Creating category with data:', categoryData);

    this.categoryService.createCategory(categoryData).subscribe({
      next: (category) => {
        console.log('Category created successfully:', category);
        this.closeCreateModal();
        this.loadCategories();
      },
      error: (error) => {
        console.error('Erreur lors de la création:', error);
        console.error('Error details:', error.error);
        alert('Erreur lors de la création de la catégorie: ' + (error.error?.detail || error.message));
      }
    });
  }

  updateCategory(): void {
    if (!this.editingCategory || !this.editingCategory.name?.trim() || !this.editingCategory.description?.trim()) {
      alert('Veuillez remplir tous les champs obligatoires (nom et description)');
      return;
    }

    if (!this.editingCategory.color || !/^#[0-9A-F]{6}$/i.test(this.editingCategory.color)) {
      alert('Veuillez sélectionner une couleur valide');
      return;
    }

    const updateData = {
      name: this.editingCategory.name.trim(),
      description: this.editingCategory.description.trim(),
      icon: this.editingCategory.icon || 'bi-tag-fill',
      color: this.editingCategory.color
    };

    console.log('Updating category with data:', updateData);

    this.categoryService.updateCategory(this.editingCategory.id, updateData).subscribe({
      next: () => {
        console.log('Category updated successfully');
        this.closeEditModal();
        this.loadCategories();
      },
      error: (error) => {
        console.error('Erreur lors de la mise à jour:', error);
        console.error('Error details:', error.error);
        alert('Erreur lors de la mise à jour de la catégorie: ' + (error.error?.detail || error.message));
      }
    });
  }

  deleteCategory(id: number): void {
    console.log('Attempting to delete category with ID:', id);
    if (!id) {
      console.error('Category ID is missing for deletion');
      alert('Erreur: Impossible de supprimer cette catégorie (ID manquant)');
      return;
    }
    if (confirm('Êtes-vous sûr de vouloir supprimer cette catégorie ?')) {
      this.categoryService.deleteCategory(id).subscribe({
        next: () => {
          this.loadCategories();
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          alert('Erreur lors de la suppression de la catégorie');
        }
      });
    }
  }


}
