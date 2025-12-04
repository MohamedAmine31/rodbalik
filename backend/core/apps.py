from django.apps import AppConfig


class CoreConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'core'
    verbose_name = 'Rod Balik Core'
    
    def ready(self):
        import core.ml_model.signals
    
